package com.ycc.thread.threadlocal;

import java.util.concurrent.CountDownLatch;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/15
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class CountDownTest {
    public static void main(String[] args) {
        CountDownLatch downLatch = new CountDownLatch(2);

        Thread t = new Thread(() -> {
            downLatch.countDown();
            System.out.println("a over");
            try {
                downLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("a all over");
        });

        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("sleep over");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            downLatch.countDown();
            System.out.println("b over");
            try {
                downLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("b all over");
        });

        t.start();
        t1.start();

        try {
            downLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("all over");
    }
}
