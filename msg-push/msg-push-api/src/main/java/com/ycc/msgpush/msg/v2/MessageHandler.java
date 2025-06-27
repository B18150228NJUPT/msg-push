package com.ycc.msgpush.msg.v2;

import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;

public interface MessageHandler {
    MessageResult handleRequest(MessageRequest request);
}