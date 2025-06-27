package com.ycc.msgpush.msg.v2;

import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;

public class RequiredFieldValidationHandler implements MessageHandler {
    private MessageHandler nextHandler;

    public void setNextHandler(MessageHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public MessageResult handleRequest(MessageRequest request) {
        // 检查消息内容是否为空
        if (request.getMessageContent() == null || request.getMessageContent().isEmpty()) {
            return new MessageResult(false, "消息内容不能为空");
        } else if (nextHandler != null) {
            // 如果有下一个处理器，则传递请求
            return nextHandler.handleRequest(request);
        } else {
            return new MessageResult(true, "必填校验通过");
        }
    }
}