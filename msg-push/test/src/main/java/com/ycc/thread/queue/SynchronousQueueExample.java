package com.ycc.thread.queue;

import java.util.concurrent.*;

public class SynchronousQueueExample {
    public static void main(String[] args) {
        // 创建公平模式的 SynchronousQueue（确保 FIFO 顺序）
        SynchronousQueue<String> queue = new SynchronousQueue<>(true);

        // 生产者线程
        Thread producer = new Thread(() -> {
            try {
                String[] items = {"数据1", "数据2", "数据3"};
                for (String item : items) {
                    System.out.println(Thread.currentThread().getName() + 
                                       " 生产: " + item + "，等待消费者接收...");
                    queue.put(item); // 阻塞直到消费者接收
                    System.out.println(Thread.currentThread().getName() + 
                                       " 数据已被接收");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "生产者");

        // 消费者线程
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println(Thread.currentThread().getName() + 
                                       " 等待生产数据...");
                    String item = queue.take(); // 阻塞直到有数据可用
                    System.out.println(Thread.currentThread().getName() + 
                                       " 消费: " + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "消费者");

        // 启动线程
        producer.start();
        // 延迟启动消费者，观察生产者阻塞现象
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        consumer.start();

        // 等待线程结束
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}