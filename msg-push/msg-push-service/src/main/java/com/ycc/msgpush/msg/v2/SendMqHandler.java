package com.ycc.msgpush.msg.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ycc.msgpush.controller.SendMqService;
import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendMqHandler implements MessageHandler {

    @Autowired
    private SendMqService sendMqService;

    public static final String sendMessageTopic = "send_message_topic";
    public static final String tagId = "tagId";

    @Override
    public MessageResult handleRequest(MessageRequest request) {
        String message = JSON.toJSONString(request, new SerializerFeature[]{SerializerFeature.WriteClassName});
        sendMqService.send(sendMessageTopic, message, tagId);
        return new MessageResult(true, "发送成功");
    }
}
