package com.ycc.msgpush.dubbo.service;

import com.ycc.msgpush.dubbo.api.DemoService;

import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}