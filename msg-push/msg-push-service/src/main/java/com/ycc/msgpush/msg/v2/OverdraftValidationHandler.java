package com.ycc.msgpush.msg.v2;

import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OverdraftValidationHandler implements MessageHandler {
    private MessageHandler nextHandler;
    private Map<String, Integer> messageCounts = new ConcurrentHashMap<>();
    private Lock lock = new ReentrantLock();

    public void setNextHandler(MessageHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public MessageResult handleRequest(MessageRequest request) {
        try {
            // 尝试获取分布式锁
            lock.lock();

            // 检查消息发送次数是否超过限制
            int count = messageCounts.getOrDefault(request.getMessageContent(), 0);
            if (count >= 10) { // 假设超过 10 次为超发
                return new MessageResult(false, "消息发送超过限制");
            }
            // 更新消息发送次数记录
            messageCounts.put(request.getMessageContent(), count + 1);

            // 如果不超发，并且有下一个处理器，则传递请求
            if (nextHandler != null) {
                return nextHandler.handleRequest(request);
            } else {
                return new MessageResult(true, "超发校验通过");
            }
        } finally {
            // 释放锁
            lock.unlock();
        }
    }
}