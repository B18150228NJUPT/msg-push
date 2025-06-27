package com.ycc.msgpush.msg.v2;

import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;

import java.util.Collections;
import java.util.Set;

public class BlacklistValidationHandler implements MessageHandler {
    private MessageHandler nextHandler;
    private Set<String> blacklist = Collections.emptySet();

    public void setNextHandler(MessageHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public void setBlacklist(Set<String> blacklist) {
        this.blacklist = blacklist;
    }

    @Override
    public MessageResult handleRequest(MessageRequest request) {
        // 检查消息内容是否在黑名单中
        if (blacklist.contains(request.getMessageContent())) {
            return new MessageResult(false, "消息内容在黑名单中");
        } else if (nextHandler != null) {
            // 如果不在黑名单中，并且有下一个处理器，则传递请求
            return nextHandler.handleRequest(request);
        } else {
            return new MessageResult(true, "黑名单校验通过");
        }
    }
}