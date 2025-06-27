package com.ycc.msgpush.msg.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

public class RocketMQMessageListenerFactory implements BeanPostProcessor {
    private RedisTemplate<String, String> redisTemplate;

    public RocketMQMessageListenerFactory(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RocketMQMessageListener annotation = AnnotationUtils.findAnnotation(bean.getClass(), RocketMQMessageListener.class);
        if (annotation != null) {
            String topic = annotation.topic();
            String consumerGroup = annotation.consumerGroup();
            if (StringUtils.hasText(topic) && StringUtils.hasText(consumerGroup)) {
                DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
                consumer.setNamesrvAddr("127.0.0.1:9876"); // 设置NameServer地址
                consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
                consumer.setMessageModel(MessageModel.CLUSTERING); // 设置消息模式为集群模式

                try {
                    consumer.subscribe(topic, "*");
                    consumer.registerMessageListener(new RocketMQMessageListenerConcurrently(bean, redisTemplate));
                    consumer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}