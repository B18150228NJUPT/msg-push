package com.ycc.msgpush.msg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlacklistValidatorTest {

    @Test
    void setNextValidator() {
        // 创建责任链节点
        MessageValidator requiredFieldValidator = new RequiredFieldValidator();
        MessageValidator dataValidator = new DataValidator();
        MessageValidator blacklistValidator = new BlacklistValidator();
        MessageValidator exceedingValidator = new ExceedingValidator();

        // 设置责任链顺序
        requiredFieldValidator.setNextValidator(dataValidator);
        dataValidator.setNextValidator(blacklistValidator);
        blacklistValidator.setNextValidator(exceedingValidator);

        // 创建消息发送请求
        MessageRequest request = new MessageRequest("Test Message");

        // 执行责任链验证
        boolean isValid = requiredFieldValidator.validate(request);
        if (isValid) {
            System.out.println("消息验证通过，可以发送。");
        } else {
            System.out.println("消息验证未通过，无法发送。");
        }
    }
}