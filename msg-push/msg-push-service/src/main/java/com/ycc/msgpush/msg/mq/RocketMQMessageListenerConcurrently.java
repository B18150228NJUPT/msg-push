package com.ycc.msgpush.msg.mq;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public class RocketMQMessageListenerConcurrently implements MessageListenerConcurrently {
    private Object listener;
    private RedisTemplate<String, String> redisTemplate;

    public RocketMQMessageListenerConcurrently(Object listener, RedisTemplate<String, String> redisTemplate) {
        this.listener = listener;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt message : msgs) {
            String messageId = message.getMsgId();
            // 使用 Redis 缓存记录已消费的消息ID
            if (redisTemplate.opsForValue().setIfAbsent(messageId, "consumed")) {
                // 执行实际的消息处理逻辑
                // 可以通过反射调用 listener 对象的方法来处理消息
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}