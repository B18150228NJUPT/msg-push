package com.ycc;

import com.ycc.dao.MessageTemplateDao;
import com.ycc.msgpush.dubbo.api.DemoService;
import com.ycc.msgpush.msg.MessageTemplate;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Consumer implements CommandLineRunner {
    @DubboReference
    private DemoService demoService;
    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Override
    @GlobalTransactional
    public void run(String... args) throws Exception {
        String result = demoService.sayHello("world");
        long l = new Random().nextLong();
        messageTemplateDao.insert(new MessageTemplate(l , "test-caller", 1, "1", 1, 1, "1", 1, 1, 1, 1, 1, "1", "1", 1, "1", "1", "1", "1", "1", 1, 1, 1));
        System.out.println("Receive result ======> " + result);
    }

  /*  @Override
    @GlobalTransactional
    public void run(String... args) throws Exception {
        long l = new Random().nextLong();
        messageTemplateDao.insert(new MessageTemplate(l , "test-caller", 1, "1", 1, 1, "1", 1, 1, 1, 1, 1, "1", "1", 1, "1", "1", "1", "1", "1", 1, 1, 1));
        String result = demoService.sayHelloOk("world");
        System.out.println("Receive result ======> " + result);
    }*/
}
