package com.ycc.msgpush.msg;

interface MessageValidator {
    void setNextValidator(MessageValidator nextValidator);
    boolean validate(MessageRequest request);
}