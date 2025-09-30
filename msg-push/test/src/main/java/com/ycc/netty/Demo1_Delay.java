package com.ycc.netty;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.TimeUnit;

public class Demo1_Delay {
    public static void main(String[] args) throws InterruptedException {
        HashedWheelTimer timer = new HashedWheelTimer(new DefaultThreadFactory("redisson-timer"),
                50, TimeUnit.MILLISECONDS, 1024, false);

        schedule(timer);

        Thread.sleep(10000);
    }

    private static void schedule(HashedWheelTimer timer) {
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("run task..");

                schedule(timer);
            }
        }, 2, TimeUnit.SECONDS);
    }
}