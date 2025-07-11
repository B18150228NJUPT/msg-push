package com.ycc.msgpush.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = "rocketmq")
public class RocketMqSendMqServiceImpl implements SendMqService {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void send(String topic, String jsonValue, String tagId) {
        if (StringUtils.isNotBlank(tagId)) {
            topic = topic + ":" + tagId;
        }
        send(topic, jsonValue);
    }

    @Override
    public void send(String topic, String jsonValue) {
        rocketMqTemplate.send(topic, MessageBuilder.withPayload(jsonValue).build());
    }
}
