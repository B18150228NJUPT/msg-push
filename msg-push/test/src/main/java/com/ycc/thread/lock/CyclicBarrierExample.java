package com.ycc.thread.lock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {

    public static void main(String[] args) {
        int participantCount = 3;
        
        // 定义 CyclicBarrier，当所有线程都到达屏障后，执行 barrierAction
        CyclicBarrier barrier = new CyclicBarrier(participantCount, () -> {
            System.out.println("所有线程已到达屏障，进行阶段性汇总或下一步操作");
        });

        // 启动多个线程
        for (int i = 1; i <= participantCount; i++) {
            new Thread(new Task(barrier, i)).start();
        }
    }

    static class Task implements Runnable {
        private final CyclicBarrier barrier;
        private final int participantId;

        public Task(CyclicBarrier barrier, int participantId) {
            this.barrier = barrier;
            this.participantId = participantId;
        }

        @Override
        public void run() {
            try {
                System.out.println("线程 " + participantId + " 正在执行阶段任务...");
                Thread.sleep((long) (Math.random() * 3000)); // 模拟耗时操作

                System.out.println("线程 " + participantId + " 到达屏障，等待其他线程...");
                barrier.await(); // 等待其他线程

                System.out.println("线程 " + participantId + " 继续执行后续任务...");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
