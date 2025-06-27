package com.ycc.msgpush.controller;

import com.ycc.msgpush.enums.AnchorState;
import com.ycc.msgpush.log.LogUtils;
import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;
import com.ycc.msgpush.msg.SendReq;
import com.ycc.msgpush.msg.v2.MessageHandler;
import com.ycc.msgpush.msg.vo.AnchorInfo;
import com.ycc.msgpush.msg.vo.MessageParam;
import com.ycc.msgpush.msg.vo.TaskInfo;
import com.ycc.msgpush.utils.TaskInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@Slf4j
public class MessageTemplateController {

    @Autowired
    @Qualifier("validationChain")
    MessageHandler messageHandler;
    @Autowired
    LogUtils logUtils;

    // 发送消息接口
    @PostMapping("/send")
    public MessageResult send(@RequestBody SendReq sendReq) {

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setBusinessId(TaskInfoUtils.generateBusinessId(sendReq.getMessageTemplateId(), sendReq.getMessageTemplate().getTemplateType()));
        taskInfo.setReceiver(Collections.singleton(sendReq.getMessageParam().getReceiver()));
        taskInfo.setMessageId(TaskInfoUtils.generateMessageId());
        taskInfo.setBizId(taskInfo.getMessageId());

        logUtils.print(AnchorInfo.builder().state(AnchorState.SEND_SUCCESS.getCode())
                .bizId(taskInfo.getBizId()).messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());

        log.info("send message: {}", sendReq);

        Long messageTemplateId = sendReq.getMessageTemplateId();
        MessageParam messageParam = sendReq.getMessageParam();
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessageTemplateId(messageTemplateId);
        messageRequest.setMessageParam(messageParam);
        messageRequest.setMessageContent("发布消息：${code}");
        messageRequest.setMessageTemplate(sendReq.getMessageTemplate());

        return messageHandler.handleRequest(messageRequest);
    }
}
