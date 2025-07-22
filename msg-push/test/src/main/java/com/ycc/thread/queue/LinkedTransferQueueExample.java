package com.ycc.thread.queue;

import java.util.concurrent.*;

public class LinkedTransferQueueExample {
    public static void main(String[] args) throws InterruptedException {
        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();

        // 消费者线程
        Thread consumer = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + 
                                   " 等待接收数据...");
                
                // 消费者提前启动并等待数据
                System.out.println(Thread.currentThread().getName() + 
                                   " 接收到: " + queue.take());
                
                // 再次等待接收
                System.out.println(Thread.currentThread().getName() + 
                                   " 接收到: " + queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "消费者");

        // 生产者线程
        Thread producer = new Thread(() -> {
            try {
                // 立即传输（消费者已等待）
                queue.transfer("数据1");
                System.out.println(Thread.currentThread().getName() + 
                                   " 已传输: 数据1");
                
                // 模拟生产者先于消费者到达
                System.out.println(Thread.currentThread().getName() + 
                                   " 准备传输: 数据2");
                queue.transfer("数据2");
                System.out.println(Thread.currentThread().getName() + 
                                   " 已传输: 数据2");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "生产者");

        // 启动消费者线程，确保其先运行
        consumer.start();
        Thread.sleep(1000); // 确保消费者先启动并等待
        
        // 启动生产者线程
        producer.start();

        // 等待线程结束
        producer.join();
        consumer.join();
    }
}