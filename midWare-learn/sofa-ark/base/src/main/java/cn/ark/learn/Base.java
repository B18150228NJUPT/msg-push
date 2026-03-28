package cn.ark.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
@SpringBootApplication
@RestController
public class Base {

    @GetMapping("/hi1")
    public String hello(HttpServletRequest request) {
        request.setAttribute("context", "parent-value");
        return "hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(Base.class, args);
    }
}
