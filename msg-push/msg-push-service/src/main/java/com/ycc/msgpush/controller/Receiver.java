package com.ycc.msgpush.controller;

import com.alibaba.fastjson.JSON;
import com.ycc.msgpush.log.LogUtils;
import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.vo.LogParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.ycc.msgpush.controller.MsgConsumeListener.LOG_BIZ_TYPE;

/**
 * 消费MQ的消息
 */
@Slf4j
//@Component
//@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = "kafka")
public class Receiver {


    @Autowired
    LogUtils logUtils;

    /**
     * 发送消息
     *
     * @param consumerRecord
     */
    @KafkaListener(topics = "send_message_topic", containerFactory = "filterContainerFactory")
//    @KafkaListener(topics = "send_message_topic")
    public void consumer(ConsumerRecord<?, String> consumerRecord, @Header(KafkaHeaders.GROUP_ID) String topicGroupId) {
        Optional<String> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        if (kafkaMessage.isPresent()) {

            MessageRequest messageRequest = JSON.parseObject(kafkaMessage.get(), MessageRequest.class);

            logUtils.print(
                    LogParam.builder().bizType(LOG_BIZ_TYPE)
                            .object(messageRequest).build()
            );
        }
    }
}
