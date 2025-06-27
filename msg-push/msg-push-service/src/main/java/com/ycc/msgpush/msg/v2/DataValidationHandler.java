package com.ycc.msgpush.msg.v2;

import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;

public class DataValidationHandler implements MessageHandler {
    private MessageHandler nextHandler;

    public void setNextHandler(MessageHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public MessageResult handleRequest(MessageRequest request) {
        // 进行数据校验，这里只是示范，具体校验逻辑根据业务需求实现
        // 如果数据校验通过，则继续向下传递请求
        // 这里假设数据校验通过的条件为消息长度不超过50个字符
        if (request.getMessageContent().length() <= 50) {
            if (nextHandler != null) {
                return nextHandler.handleRequest(request);
            } else {
                return new MessageResult(true, "数据校验通过");
            }
        } else {
            return new MessageResult(false, "消息长度超过50个字符");
        }
    }
}