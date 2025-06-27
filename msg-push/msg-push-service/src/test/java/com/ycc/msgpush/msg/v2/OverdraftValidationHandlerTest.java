package com.ycc.msgpush.msg.v2;

import com.ycc.msgpush.msg.MessageRequest;
import com.ycc.msgpush.msg.MessageResult;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OverdraftValidationHandlerTest {

    @Test
    void setNextHandler() {
        // 创建责任链节点
        RequiredFieldValidationHandler requiredFieldHandler = new RequiredFieldValidationHandler();
        DataValidationHandler dataHandler = new DataValidationHandler();
        BlacklistValidationHandler blacklistHandler = new BlacklistValidationHandler();
        OverdraftValidationHandler overdraftHandler = new OverdraftValidationHandler();

        // 设置责任链节点的顺序
        requiredFieldHandler.setNextHandler(dataHandler);
        dataHandler.setNextHandler(blacklistHandler);
        blacklistHandler.setNextHandler(overdraftHandler);

        // 初始化黑名单
        Set<String> blacklist = new HashSet<>();
        blacklist.add("spam");
        blacklist.add("virus");
        blacklistHandler.setBlacklist(blacklist);

        // 创建消息请求
        MessageRequest request = new MessageRequest("Important message");

        // 处理消息请求
        MessageResult result = requiredFieldHandler.handleRequest(request);
        System.out.println("处理结果: " + result.isSuccess() + ", " + result.getMessage());
    }
}