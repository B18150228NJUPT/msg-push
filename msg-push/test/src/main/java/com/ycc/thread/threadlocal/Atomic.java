package com.ycc.thread.threadlocal;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/13
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class Atomic {
    public static void main(String[] args) {
        AtomicReference<String> atomicRef = new AtomicReference<>("initial value");

        String s = atomicRef.get();
        atomicRef.set("new value");

        boolean b = atomicRef.compareAndSet(s, "11");

    }
}
