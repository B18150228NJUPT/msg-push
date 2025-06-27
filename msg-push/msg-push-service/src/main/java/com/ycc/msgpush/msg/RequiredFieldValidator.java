package com.ycc.msgpush.msg;

class RequiredFieldValidator implements MessageValidator {
    private MessageValidator nextValidator;

    @Override
    public void setNextValidator(MessageValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public boolean validate(MessageRequest request) {
        // 进行必填校验逻辑
        if (request.getMessageContent() == null || request.getMessageContent().isEmpty()) {
            System.out.println("消息内容不能为空");
            return false;
        } else {
            if (nextValidator != null) {
                return nextValidator.validate(request);
            }
            return true;
        }
    }
}