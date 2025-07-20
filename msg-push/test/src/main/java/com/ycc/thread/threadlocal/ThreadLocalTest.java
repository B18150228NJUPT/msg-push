package com.ycc.thread.threadlocal;

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
    }
}
