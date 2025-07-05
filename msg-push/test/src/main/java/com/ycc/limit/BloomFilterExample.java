package com.ycc.limit;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.StandardCharsets;

public class BloomFilterExample {
    public static void main(String[] args) {
        // 1. 定义布隆过滤器参数
        int expectedInsertions = 1000000;  // 预计插入元素数量
        double fpp = 0.01;                 // 期望误判率（1%）
        
        // 2. 创建布隆过滤器（存储字符串）
        BloomFilter<String> bloomFilter = BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8),  // 元素类型的Funnel
            expectedInsertions,      // 预计元素数量
            fpp                      // 误判率
        );
        
        // 3. 插入元素
        String[] urls = {
            "https://example.com/page1",
            "https://example.com/page2",
            "https://example.net/home"
        };
        
        for (String url : urls) {
            bloomFilter.put(url);
        }
        
        // 4. 验证元素是否存在
        String existsUrl = "https://example.com/page1";
        String notExistsUrl = "https://example.org/new";
        
        System.out.println(existsUrl + " 是否存在: " + bloomFilter.mightContain(existsUrl));
        System.out.println(notExistsUrl + " 是否存在: " + bloomFilter.mightContain(notExistsUrl));
        
        // 5. 模拟误判（概率性出现）
        int falsePositiveCount = 0;
        for (int i = 0; i < 10000; i++) {
            String fakeUrl = "https://fake.com/" + i;
            if (bloomFilter.mightContain(fakeUrl)) {
                falsePositiveCount++;
            }
        }
        
        System.out.println("误判率测试：10000个不存在的URL中，" + falsePositiveCount + "个被误判为存在");
        System.out.println("实际误判率: " + (double) falsePositiveCount / 10000);
    }
}