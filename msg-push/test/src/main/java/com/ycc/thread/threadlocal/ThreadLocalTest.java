package com.ycc.thread.threadlocal;

import com.alibaba.ttl.TtlRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/15
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class ThreadLocalTest {
    public static void main(String[] args) {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("hello");
        threadLocal.remove();


        // 2. 线程池
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // 3. 父线程设置值
        threadLocal.set("parent-value");

        // 4. 提交任务时，用 TtlRunnable 包装
        executor.submit(TtlRunnable.get(() -> {
            System.out.println("线程池任务获取值：" + threadLocal.get()); // 输出 "parent-value"
        }));

        // 5. 父线程修改值后再次提交
        threadLocal.set("parent-new-value");
        executor.submit(TtlRunnable.get(() -> {
            System.out.println("线程池任务获取新值：" + threadLocal.get()); // 输出 "parent-new-value"
        }));

        executor.shutdown();
    }
}
