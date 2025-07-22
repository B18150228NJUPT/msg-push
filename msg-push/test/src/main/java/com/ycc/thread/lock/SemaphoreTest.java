package com.ycc.thread.lock;

import java.util.concurrent.Semaphore;

public class SemaphoreTest {
    public static void main(String[] args) {
        // 设置信号量许可数为3，表示最多允许3个线程同时执行
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 6; i++) {
            int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("线程 " + Thread.currentThread().getName() + " 正在尝试获取信号量...");
                    semaphore.acquire(); // 获取许可
                    System.out.println("线程 " + Thread.currentThread().getName() + " 获取到信号量");

                    // 模拟任务执行
                    Thread.sleep(2000);

                    // 释放许可
                    semaphore.release();
                    System.out.println("线程 " + Thread.currentThread().getName() + " 释放了信号量");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "Thread-" + threadId).start();
        }

        // 监控等待队列中的线程数量
        while (true) {
            System.out.println("当前等待中的线程数: " + semaphore.getQueueLength());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
