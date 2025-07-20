package com.ycc.thread.threadlocal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/12
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class Lock {

    public static void main(String[] args) throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        new Thread(() -> {
            readLock.lock();
            try {
                System.out.println("t1 获取读锁");
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                readLock.unlock();
            }
        }).start();

        new Thread(() -> {
            readLock.lock();
            try {
                System.out.println("t2 获取读锁");
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                readLock.unlock();
            }
        }).start();

        TimeUnit.SECONDS.sleep(10);
    }
}
