package com.ycc.thread.queue;

import java.util.concurrent.*;

public class LinkedBlockingDequeExample {
    public static void main(String[] args) throws InterruptedException {
        // 创建容量为3的有界双向队列
        LinkedBlockingDeque<String> deque = new LinkedBlockingDeque<>(3);

        // 生产者线程 - 从头部插入
        Thread producerFront = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    String item = "前插-" + i;
                    deque.putFirst(item); // 阻塞直到有空间
                    System.out.println(Thread.currentThread().getName() + 
                                       " 插入头部: " + item);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "前插生产者");

        // 生产者线程 - 从尾部插入
        Thread producerRear = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    String item = "后插-" + i;
                    deque.putLast(item); // 阻塞直到有空间
                    System.out.println(Thread.currentThread().getName() + 
                                       " 插入尾部: " + item);
                    Thread.sleep(700);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "后插生产者");

        // 消费者线程 - 从头部消费
        Thread consumerFront = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String item = deque.takeFirst(); // 阻塞直到有元素
                    System.out.println(Thread.currentThread().getName() + 
                                       " 消费头部: " + item);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "前插消费者");

        // 消费者线程 - 从尾部消费
        Thread consumerRear = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String item = deque.takeLast(); // 阻塞直到有元素
                    System.out.println(Thread.currentThread().getName() + 
                                       " 消费尾部: " + item);
                    Thread.sleep(1200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "后插消费者");

        // 启动所有线程
        producerFront.start();
        producerRear.start();
        Thread.sleep(100); // 确保生产者先启动
        consumerFront.start();
        consumerRear.start();

        // 等待所有线程完成
        producerFront.join();
        producerRear.join();
        consumerFront.join();
        consumerRear.join();
    }
}