package com.ycc.thread.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/10
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class TransmittableThreadLocalTest {

    // 1. 定义 TTL 变量
    public static TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();

    public static void main(String[] args) {
        // 2. 线程池
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // 3. 父线程设置值
        context.set("parent-value");

        // 4. 提交任务时，用 TtlRunnable 包装
        executor.submit(TtlRunnable.get(() -> {
            System.out.println("线程池任务获取值：" + context.get()); // 输出 "parent-value"
        }));

        // 5. 父线程修改值后再次提交
        context.set("parent-new-value");
        executor.submit(TtlRunnable.get(() -> {
            System.out.println("线程池任务获取新值：" + context.get()); // 输出 "parent-new-value"
        }));

        context.set("123");
        Object captured = TransmittableThreadLocal.Transmitter.capture();
        System.out.println("captured: " + captured);

        executor.submit((() -> {
            TransmittableThreadLocal.Transmitter.replay(captured);
            System.out.println("线程池任务获取值：" + context.get()); // 输出 "parent-value"
        }));

        executor.shutdown();
    }
}
