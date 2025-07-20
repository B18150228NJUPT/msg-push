package com.ycc.thread.threadlocal;

public class PrintAB {
    // 共享锁对象（必须是同一个对象）
    private final Object lock = new Object();
    // 状态标记：true->线程A执行，false->线程B执行
    private boolean flag = true;
    // 控制循环次数（避免无限打印，可按需调整）
    private static final int MAX_COUNT = 10;
    private int count = 0;

    // 线程A：打印"a"
    class ThreadA extends Thread {
        @Override
        public void run() {
            while (count < MAX_COUNT) {
                synchronized (lock) {
                    try {
                        // 如果不该自己执行，等待
                        while (!flag) {
                            lock.wait(); // 释放锁，进入等待状态
                        }
                        // 打印"a"
                        System.out.print("a");
                        count++;
                        // 修改状态，通知线程B
                        flag = false;
                        lock.notify(); // 唤醒等待的线程B
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 线程B：打印"b"
    class ThreadB extends Thread {
        @Override
        public void run() {
            while (count < MAX_COUNT) {
                synchronized (lock) {
                    try {
                        // 如果不该自己执行，等待
                        while (flag) {
                            lock.wait(); // 释放锁，进入等待状态
                        }
                        // 打印"b"
                        System.out.print("b");
                        count++;
                        // 修改状态，通知线程A
                        flag = true;
                        lock.notify(); // 唤醒等待的线程A
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        PrintAB printer = new PrintAB();
        Thread threadA = printer.getThreadA();
        Thread threadB = printer.getThreadB();
        threadA.start();
        threadB.start();
    }

    private Thread getThreadB() {
        return new ThreadB();
    }

    private Thread getThreadA() {
        return new ThreadA();
    }
}