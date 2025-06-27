package com.ycc.msgpush.flowController;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterExample {
    public static void main(String[] args) {
        // 设置每秒生成 5 个令牌（即每秒允许 5 次请求）
        RateLimiter rateLimiter = RateLimiter.create(5.0);

        for (int i = 0; i < 10; i++) {
            double waitTime = rateLimiter.acquire(1); // 获取一个令牌
            System.out.println("Waited for " + waitTime + " seconds");
        }
    }
}
