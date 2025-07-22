package com.ycc.msgpush;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.dromara.dynamictp.spring.annotation.EnableDynamicTp;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@EnableDubbo
//@EnableDynamicThreadPool
@EnableDynamicTp
@MapperScan
@ServletComponentScan
public class MsgPushApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsgPushApplication.class, args);
    }

}
