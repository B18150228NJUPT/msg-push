package com.ycc.msgpush.msg;

public class MessageResult {
    boolean success;
    private String message;

    public MessageResult( boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
}
