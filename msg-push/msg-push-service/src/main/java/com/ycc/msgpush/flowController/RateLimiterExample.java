package com.ycc.msgpush.flowController;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

public class RateLimiterExample {
    public static void main(String[] args) {
        // 设置每秒生成 5 个令牌（即每秒允许 5 次请求）
        RateLimiter rateLimiter = RateLimiter.create(2.0);

        long l = System.nanoTime();
        for (int i = 0; i < 8; i++) {
            double waitTime = rateLimiter.acquire(1); // 获取一个令牌
            System.out.println("Waited for " + waitTime + " seconds");
        }
        System.out.println(System.nanoTime() - l);

        // 创建允许3秒突发的限流器（桶容量=2×3=6）
        RateLimiter limiter = RateLimiter.create(2.0, 3, TimeUnit.SECONDS);

        long l1 = System.nanoTime();
        // 突发请求测试
        for (int i = 0; i < 8; i++) {
            double waitTime = limiter.acquire();
            System.out.printf("请求 %d 等待时间: %.2f 秒%n", i, waitTime);
        }
        System.out.println(System.nanoTime() - l1);
    }
}
