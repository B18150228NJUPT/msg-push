package com.ycc.thread.collection;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/13
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class Map {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("1", "1");
        Object o = map.get("1");


        HashMap<Integer, Object> map2 = new HashMap<>();
        new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                Object o1 = map2.get(i);
                System.out.println(o1);
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                map2.put(i , i);
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                Object o1 = map2.get(i);
                System.out.println(o1);
            }
        }).start();
        Thread.sleep(100000);
    }
}
