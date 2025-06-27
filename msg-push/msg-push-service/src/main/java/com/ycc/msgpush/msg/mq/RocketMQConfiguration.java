package com.ycc.msgpush.msg.mq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RocketMQConfiguration {
//    @Bean
    public RocketMQMessageListenerFactory rocketMQMessageListenerFactory(RedisTemplate<String, String> redisTemplate) {
        return new RocketMQMessageListenerFactory(redisTemplate);
    }
}