package com.ycc.msgpush.filter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedRunnable;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@WebFilter(urlPatterns = "/*")
public class CircuitBreakerFilter implements Filter {

    // 全局注册表，可换成 Spring 单例
    private static final CircuitBreakerRegistry registry =
            CircuitBreakerRegistry.ofDefaults();

    // 每个 URL 模式一个 CB，这里简单演示：固定一个
    private static final CircuitBreaker cb = registry.circuitBreaker("global",
            CircuitBreakerConfig.custom()
                    // # 最近 10 次调用
                    .slidingWindowSize(10)
                    // 失败率 > 50% 触发 熔断器开启
                    .failureRateThreshold(50.0f)
                    // 熔断器开启 10 s 后转为半开启
                    .waitDurationInOpenState(Duration.ofSeconds(10))
                    // 半开启状态下，允许 3 次调用
                    .permittedNumberOfCallsInHalfOpenState(3)
                    .build());

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 用 Resilience4j 的 Supplier 包装真正的业务调用
        CheckedRunnable checkedRunnable = CircuitBreaker
                .decorateCheckedRunnable(cb, () -> chain.doFilter(req, res));

        try {
            // 正常放行
            checkedRunnable.run();
        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }

    /* 可选：初始化 / 销毁钩子 */
    @Override
    public void init(FilterConfig cfg) {
    }

    @Override
    public void destroy() {
    }
}