package com.ycc.msgpush.controller;

import com.alibaba.fastjson2.JSON;
import com.ycc.msgpush.log.LogUtils;
import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageTemplate;
import com.ycc.msgpush.msg.v2.MessageHandler;
import com.ycc.msgpush.msg.vo.LogParam;
import com.ycc.msgpush.threadPool.ThreadPoolExecutorManager;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "send_message_topic", consumerGroup = "send_message_group",
        selectorType = SelectorType.TAG, selectorExpression = "tagId")
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = "rocketmq")
public class MsgConsumeListener implements RocketMQListener<String> {

    public static final String LOG_BIZ_TYPE = "Receiver#consumer";

    @Autowired
    LogUtils logUtils;
    @Autowired
    ThreadPoolExecutorManager threadPoolExecutorManager;

    @Autowired
    @Qualifier("taskValidationChain")
    MessageHandler taskValidationChain;

    @Override
    public void onMessage(String s) {
        MessageRequest messageRequest = JSON.parseObject(s, MessageRequest.class);

        logUtils.print(
                LogParam.builder().bizType(LOG_BIZ_TYPE)
                        .object(messageRequest).build()
        );

        MessageTemplate messageTemplate = messageRequest.getMessageTemplate();
        String topicGroupId = messageTemplate.getSendChannel() + ":" + messageTemplate.getMsgType();
        threadPoolExecutorManager.route(topicGroupId).execute(() -> {
            taskValidationChain.handleRequest(messageRequest);
        });
    }
}
