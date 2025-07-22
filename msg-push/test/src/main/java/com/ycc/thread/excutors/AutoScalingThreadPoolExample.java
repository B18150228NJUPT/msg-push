package com.ycc.thread.excutors;

import java.util.concurrent.*;

public class AutoScalingThreadPoolExample {
    public static void main(String[] args) throws InterruptedException {
        // 创建线程池，核心线程数为3，最大线程数为5，空闲线程存活时间为10秒
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3,  // 核心线程数
                5,  // 最大线程数
                10, // 空闲线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10)  // 任务队列
        );

        // 允许核心线程超时
        executor.allowCoreThreadTimeOut(true);

        // 提交一些任务1
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " is running on thread " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task " + taskId + " completed.");
            });
        }

        // 等待所有任务完成
        Thread.sleep(15000);

        // 打印线程池信息
        System.out.println("Active threads after tasks: " + executor.getActiveCount());
        System.out.println("Pool size after tasks: " + executor.getPoolSize());

        // 提交一些任务
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " is running on thread " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task " + taskId + " completed.");
            });
        }

        // 关闭线程池
        executor.shutdown();
    }
}