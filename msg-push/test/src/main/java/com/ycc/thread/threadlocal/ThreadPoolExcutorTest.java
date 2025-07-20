package com.ycc.thread.threadlocal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/15
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class ThreadPoolExcutorTest {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1
                , 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                
            }
        });
    }
}
