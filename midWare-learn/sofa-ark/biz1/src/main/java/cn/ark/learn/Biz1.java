package cn.ark.learn;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
public class Biz1 {

	@GetMapping("/hi1")
	public String hello(HttpServletRequest request) {
		request.setAttribute("context", "parent-value");
		return "hello world Biz1";
	}

	public static void main(String[] args) {
		try {
			Class<?> aClass = Class.forName("javax.servlet.Filter");
			System.out.println(aClass);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		SpringApplication.run(Biz1.class, args);
	}
}
