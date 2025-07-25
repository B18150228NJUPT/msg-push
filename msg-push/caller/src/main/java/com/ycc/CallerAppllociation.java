package com.ycc;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@MapperScan
public class CallerAppllociation {
    public static void main(String[] args) {
        SpringApplication.run(CallerAppllociation.class, args);
    }

}