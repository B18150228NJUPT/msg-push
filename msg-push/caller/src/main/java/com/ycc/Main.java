package com.ycc;

import com.ycc.msgpush.dubbo.api.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }

}