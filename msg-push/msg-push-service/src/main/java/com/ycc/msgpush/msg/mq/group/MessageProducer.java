package com.ycc.msgpush.msg.mq.group;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.CountDownLatch;

public class MessageProducer {
    public static void main(String[] args) throws Exception {
        // 创建生产者
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroup");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();

        // 构造消息
        String topic = "SMSVerificationCodeTopic";
        String content = "Your verification code is: 123456";
        Message message = new Message(topic, "TagA", content.getBytes());

        // 发送消息
        int messageCount = 100; // 发送消息的数量
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        for (int i = 0; i < messageCount; i++) {
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();
                    System.out.println("Send success: " + sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    countDownLatch.countDown();
                    System.err.println("Send failed: " + e.getMessage());
                }
            });
        }

        // 等待所有消息发送完成
        countDownLatch.await();

        // 关闭生产者
        producer.shutdown();
    }
}
