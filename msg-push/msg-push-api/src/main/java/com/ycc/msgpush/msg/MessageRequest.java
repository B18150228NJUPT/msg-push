package com.ycc.msgpush.msg;

import com.ycc.msgpush.msg.vo.MessageParam;
import lombok.Data;

@Data
public class MessageRequest {
    private String messageContent;
    Long messageTemplateId;
    MessageParam messageParam;
    MessageTemplate messageTemplate;

    public MessageRequest() {
    }

    public MessageRequest(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    public Long getMessageTemplateId() {
        return messageTemplateId;
    }
    public void setMessageTemplateId(Long messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }
    public MessageParam getMessageParam() {
        return messageParam;
    }
    public void setMessageParam(MessageParam messageParam) {
        this.messageParam = messageParam;
    }

}