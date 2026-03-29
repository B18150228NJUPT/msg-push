package com.ycc.snowflake;

public class SnowFlakeUtil {

    public static void main(String[] args) {

        // 手动配置 workerId 和 datacenterId
        Snowflake snowflake = new Snowflake(1, 1);
        long l = System.nanoTime();
        for (int i = 0; i < 100_0000; i++) {
            snowflake.nextId();
        }
        System.out.println((System.nanoTime() - l) / 1000_000);
    }
}
