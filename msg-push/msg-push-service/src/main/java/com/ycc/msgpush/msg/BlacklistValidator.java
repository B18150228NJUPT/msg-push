package com.ycc.msgpush.msg;

class BlacklistValidator implements MessageValidator {
    private MessageValidator nextValidator;

    @Override
    public void setNextValidator(MessageValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public boolean validate(MessageRequest request) {
        // 进行黑名单校验逻辑
        // 这里可以添加黑名单校验逻辑
        if (nextValidator != null) {
            return nextValidator.validate(request);
        }
        return true;
    }
}