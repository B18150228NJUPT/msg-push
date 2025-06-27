package com.ycc.msgpush.controller;


import com.ycc.msgpush.channelHandler.SendMsgHandler;
import com.ycc.msgpush.msg.v2.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationChainConfig {

    @Bean("validationChain")
    public MessageHandler validationChain(SendMqHandler sendMqHandler) {
        RequiredFieldValidationHandler requiredFieldHandler = new RequiredFieldValidationHandler();
        DataValidationHandler dataHandler = new DataValidationHandler();
        BlacklistValidationHandler blacklistHandler = new BlacklistValidationHandler();
        OverdraftValidationHandler overdraftHandler = new OverdraftValidationHandler();

        // 设置责任链节点的顺序
        requiredFieldHandler.setNextHandler(dataHandler);
        dataHandler.setNextHandler(blacklistHandler);
        blacklistHandler.setNextHandler(overdraftHandler);
        overdraftHandler.setNextHandler(sendMqHandler);

        return requiredFieldHandler;
    }

    @Bean("taskValidationChain")
    public MessageHandler taskValidationChain(SendMsgHandler sendMsgHandler) {
        return sendMsgHandler;
    }
}
