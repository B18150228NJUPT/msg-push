package com.ycc.msgpush.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ycc.msgpush.dao.TrainTicketMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TrainTicketService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TrainTicketMapper mapper;

    private final Cache<String, String> localCache = Caffeine.newBuilder()
                                                             .maximumSize(10_000)
                                                             .expireAfterWrite(30, TimeUnit.SECONDS)
                                                             .build();

    /**
     * 查询车票：Redis 查询 + 熔断降级
     */
    @CircuitBreaker(name = "redisQuery", fallbackMethod = "fallback")
    @TimeLimiter(name = "redisQuery")          // 超时也算失败
    public CompletableFuture<String> queryTicket(String date, String from, String to) {
        return CompletableFuture.supplyAsync(() -> {
            String key = "train:" + date + ":" + from + "-" + to;
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) return json;

            json = mapper.selectTicket(date, from, to);
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(5));
            return json;
        });
    }

    /* 降级方法：参数/返回类型必须与原方法一致 */
    public CompletableFuture<String> fallback(String date, String from, String to,
                                              Exception ex) {
        log.warn("Redis circuit opened or timeout, fallback to local cache", ex);
        return CompletableFuture.completedFuture(
                localCache.get(key(date, from, to),
                               k -> mapper.selectTicket(date, from, to)));
    }

    private String key(String d, String f, String t) {
        return "train:" + d + ":" + f + "-" + t;
    }
}