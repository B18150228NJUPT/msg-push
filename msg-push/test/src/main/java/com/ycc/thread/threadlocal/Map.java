package com.ycc.thread.threadlocal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/13
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class Map {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("1", "1");
        Object o = map.get("1");
    }
}
