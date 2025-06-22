package com.ycc.msgpush.dubbo.api;

class MessageRequest {
    private String messageContent;

    public MessageRequest(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageContent() {
        return messageContent;
    }
}