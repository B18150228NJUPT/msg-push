package com.ycc.msgpush.channelHandler;

import com.alibaba.google.common.collect.Sets;
import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;
import com.ycc.msgpush.msg.MessageTemplate;
import com.ycc.msgpush.msg.v2.MessageHandler;
import com.ycc.msgpush.msg.vo.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendMsgHandler implements MessageHandler {

    @Autowired
    private HandlerHolder handlerHolder;

    @Override
    public MessageResult handleRequest(MessageRequest request) {
        MessageTemplate messageTemplate = request.getMessageTemplate();
        String[] split = request.getMessageParam().getReceiver().split(",");
        TaskInfo taskInfo = TaskInfo.builder()
                .messageTemplateId(request.getMessageTemplateId())
                .businessId(messageTemplate.getId())
                .receiver(Sets.newHashSet(split))
                .idType(messageTemplate.getIdType())
                .sendChannel(messageTemplate.getSendChannel())
                .templateType(messageTemplate.getTemplateType())
                .msgType(messageTemplate.getMsgType())
                .shieldType(messageTemplate.getShieldType())
                .sendAccount(messageTemplate.getSendAccount()).build();

        Handler route = handlerHolder.route(messageTemplate.getSendChannel());
        route.doHandler(taskInfo);
        return new MessageResult(true, "发送成功");
    }
}
