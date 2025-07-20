package com.ycc.thread.threadlocal;

import com.alibaba.nacos.common.executor.ExecutorFactory;
import com.alibaba.nacos.common.executor.NameThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/13
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class ThreadTest {
    public static void main(String[] args) {
         ScheduledExecutorService LONG_POLLING_EXECUTOR = ExecutorFactory.Managed.newSingleScheduledExecutorService(
                "config", new NameThreadFactory("com.alibaba.nacos.config.LongPolling"));
         LONG_POLLING_EXECUTOR.schedule(() -> {
             System.out.println("执行任务");
         }, 10, java.util.concurrent.TimeUnit.SECONDS);

        LONG_POLLING_EXECUTOR.execute(() -> {
            System.out.println("执行任务execute");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("--------------");
    }
}
