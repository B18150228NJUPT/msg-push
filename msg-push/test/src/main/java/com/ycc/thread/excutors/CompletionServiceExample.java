package com.ycc.thread.excutors;

import java.util.concurrent.*;

public class CompletionServiceExample {
    public static void main(String[] args) {
        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // 创建一个 ExecutorCompletionService 基于指定的线程池
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executorService);

        // 提交一组异步任务
        for (int i = 0; i < 10; i++) {
            final int index = i;
            completionService.submit(() -> {
                // 模拟任务执行时间
                Thread.sleep((long) (Math.random() * 1000));
                // 返回结果
                return index * index;
            });
        }

        // 逐步获取任务结果
        for (int i = 0; i < 10; i++) {
            try {
                // 检索并移除已完成的任务结果，如果没有则会阻塞
                Future<Integer> future = completionService.take();
                Integer result = future.get();
                System.out.println("Result of task: " + result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 关闭线程池
        executorService.shutdown();
    }
}