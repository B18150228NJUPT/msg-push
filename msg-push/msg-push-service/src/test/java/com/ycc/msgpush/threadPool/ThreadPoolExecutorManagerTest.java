package com.ycc.msgpush.threadPool;

import org.dromara.dynamictp.core.executor.DtpExecutor;
import org.junit.jupiter.api.Test;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/8
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
class ThreadPoolExecutorManagerTest {

    @Test
    void getExecutor() {
        DtpExecutor executor = ThreadPoolExecutorManager.getExecutor("1");

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 打印线程池信息
                System.out.println("thread-pool-name" + executor.getThreadPoolName());
                System.out.println("core-pool-size" + executor.getCorePoolSize());
                System.out.println("maximum-pool-size" + executor.getMaximumPoolSize());
                System.out.println("queue-capacity" + executor.getQueue().size());
                System.out.println("pool-size" + executor.getPoolSize());
                System.out.println("active-count" + executor.getActiveCount());
                System.out.println("task-count" + executor.getTaskCount());
                System.out.println("completed task count" + executor.getCompletedTaskCount());
                System.out.println("queue size" + executor.getQueue().size());
                System.out.println("remaingin capacity" + executor.getQueue().remainingCapacity());
                System.out.println("queue is empty" + executor.getQueue().isEmpty());
                System.out.println("=======================================");
            }
        });
        thread.start();


        for (int i = 0; i < 100000; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("===> task run in : " + Thread.currentThread().getName());
            });
        }
        while (true) {

        }
    }
}