package com.ycc.msgpush.filter;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class BulkheadHandlerInterceptor implements HandlerInterceptor, ApplicationListener<ContextRefreshedEvent> {

    private static final BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
    private static final Map<Method, Bulkhead> bulkheads = new HashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 3. 尝试进入 bulkhead
            Bulkhead bulkhead = bulkheads.get(handlerMethod.getMethod());
            boolean permitted = bulkhead.tryAcquirePermission();
            if (!permitted) {
                throw new RuntimeException("请求限制");
            }

            // 5. 成功获取许可，继续执行
            return true;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            bulkheads.get(handlerMethod.getMethod()).onComplete();
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            bulkheads.get(handlerMethod.getMethod()).onComplete();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, RequestMappingHandlerMapping> beansOfType = applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);
        beansOfType.forEach((k, v) -> v.getHandlerMethods().forEach((k1, v1) -> {
            Method method = v1.getMethod();

            // 2. 信号量隔离：最多 5 个并发
            Bulkhead bulkhead = registry.bulkhead(
                    method.getName(),
                    BulkheadConfig.custom()
                            .maxConcurrentCalls(5)
                            .maxWaitDuration(java.time.Duration.ZERO) // 0 = 立即失败
                            .build());

            bulkheads.put(method, bulkhead);
        }));
    }
}