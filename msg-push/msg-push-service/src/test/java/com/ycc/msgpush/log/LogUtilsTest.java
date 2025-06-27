package com.ycc.msgpush.log;

import com.ycc.msgpush.msg.vo.SendRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LogUtilsTest {

    @Test
    void printMsg() {
        LogUtils logUtils = new LogUtils();
        SendRequest sendRequest = new SendRequest();
        sendRequest.setMessageTemplateId(1l);
        logUtils.printMsg(sendRequest);
    }
}