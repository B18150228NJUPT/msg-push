package com.ycc.msgpush.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraylogTest {

    private static final Logger logger = LoggerFactory.getLogger(GraylogTest.class);

    public static void main(String[] args) {
        // 发送不同级别的日志到 Graylog
        logger.trace("This is a TRACE message.");
        logger.debug("This is a DEBUG message.");
        logger.info("This is an INFO message.");
        logger.warn("This is a WARN message.");
        logger.error("This is an ERROR message.");

        System.out.println("Test logs sent to Graylog.");
    }
}
