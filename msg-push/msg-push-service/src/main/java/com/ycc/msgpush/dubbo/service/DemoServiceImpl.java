package com.ycc.msgpush.dubbo.service;

import com.ycc.msgpush.dao.MessageTemplateDao;
import com.ycc.msgpush.dubbo.api.DemoService;

import com.ycc.msgpush.msg.MessageTemplate;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

@DubboService
public class DemoServiceImpl implements DemoService {


    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Override
    public String sayHello(String name) {
        long l = new Random().nextLong();
        messageTemplateDao.insert(new MessageTemplate(l, "test", 1, "1", 1, 1, "1", 1, 1, 1, 1, 1, "1", "1", 1, "1", "1", "1", "1", "1", 1, 1, 1));
        throw new RuntimeException("测试异常");
//        return "Hello " + name;
    }

    @Override
    public String sayHelloOk(String name) {
        long l = new Random().nextLong();
        messageTemplateDao.insert(new MessageTemplate(l, "test", 1, "1", 1, 1, "1", 1, 1, 1, 1, 1, "1", "1", 1, "1", "1", "1", "1", "1", 1, 1, 1));
        return "Hello " + name;
    }
}