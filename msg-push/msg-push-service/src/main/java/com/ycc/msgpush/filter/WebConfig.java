package com.ycc.msgpush.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private BulkheadHandlerInterceptor bulkheadInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bulkheadInterceptor)
                .addPathPatterns("/**")   // 拦截所有请求
                .excludePathPatterns("/actuator/**"); // 排除监控
    }
}