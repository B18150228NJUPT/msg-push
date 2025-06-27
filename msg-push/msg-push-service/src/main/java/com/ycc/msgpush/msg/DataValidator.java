package com.ycc.msgpush.msg;

class DataValidator implements MessageValidator {
    private MessageValidator nextValidator;

    @Override
    public void setNextValidator(MessageValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public boolean validate(MessageRequest request) {
        // 进行数据校验逻辑
        // 这里可以添加针对数据的校验逻辑
        if (nextValidator != null) {
            return nextValidator.validate(request);
        }
        return true;
    }
}