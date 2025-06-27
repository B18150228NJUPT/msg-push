package com.ycc.msgpush.msg.persistent;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MySQLBinlogToRocketMQWithCompensation {

    private static final ConcurrentHashMap<Long, List<org.apache.rocketmq.common.message.Message>> unprocessedMessages = new ConcurrentHashMap<>();

    public static void main(String[] args) throws MQClientException {
        // 创建 Canal 连接
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(AddressUtils.getHostIp(), 11111),
                "example", "", "");

        // 创建 RocketMQ 生产者
        DefaultMQProducer producer = new DefaultMQProducer("BinlogProducerGroup");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();

        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();

            while (true) {
                Message message = connector.getWithoutAck(100);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    processBinlogEntries(message.getEntries(), producer);
                }
                connector.ack(batchId);
            }
        } finally {
            producer.shutdown();
            connector.disconnect();
        }
    }

    private static void processBinlogEntries(List<CanalEntry.Entry> entries, DefaultMQProducer producer) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                try {
                    CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                    List<MessageExt> messagesToSend = new ArrayList<>();

                    for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                        if (rowChange.getEventType() == CanalEntry.EventType.INSERT) {
                            // 构造消息
                            String tableName = entry.getHeader().getTableName();
                            String data = rowData.getAfterColumnsList().toString();
                            MessageExt message = new MessageExt();
                            message.setTopic(tableName);
                            message.setBody(data.getBytes());

                            messagesToSend.add(message);
                        }
                    }

                    if (!messagesToSend.isEmpty()) {
                        sendMessagesWithRetry(messagesToSend, producer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void sendMessagesWithRetry(List<MessageExt> messages, DefaultMQProducer producer) {
        for (MessageExt message : messages) {
            try {
                producer.send(message);
            } catch (Exception e) {
                handleSendFailure(message);
            }
        }
    }

    private static void handleSendFailure(org.apache.rocketmq.common.message.Message message) {
        long currentTimeMillis = System.currentTimeMillis();
        List<org.apache.rocketmq.common.message.Message> unprocessed = unprocessedMessages.getOrDefault(currentTimeMillis, new ArrayList<>());
        unprocessed.add(message);
        unprocessedMessages.put(currentTimeMillis, unprocessed);
    }
}
