package com.ycc.msgpush.msg.mq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketMQMessageListener {
    String topic() default ""; // RocketMQ Topic
    String consumerGroup(); // RocketMQ Consumer Group
}