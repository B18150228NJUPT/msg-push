package org.ycc;

import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
@SpringBootApplication
@RestController
@EnableFeignClients
public class App1 {

    // 1. 定义 TTL 变量
    public static TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();

    @Autowired
    HiFeign hiFeign;

//    @FeignClient(name = "app2", url = "http://127.0.0.1:8082")
    @FeignClient(name = "app2")
    interface HiFeign {
        @GetMapping("/hi")
        String hello();
    }

    @GetMapping("/hi1")
    public String hello(HttpServletRequest request) {
        // ((RequestData) (((DefaultRequestContext)request.getContext()).getClientRequest())).getHeaders().get("111")
        request.setAttribute("context", "parent-value");
        context.set("parent-value");
        hiFeign.hello();
        return "hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(App1.class, args);
    }
}
