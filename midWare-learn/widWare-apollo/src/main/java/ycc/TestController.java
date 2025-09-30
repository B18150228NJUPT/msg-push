package ycc;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alicp.jetcache.anno.Cached;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // 基础流量控制示例
    @GetMapping("/hello")
    @SentinelResource(value = "hello", blockHandler = "helloBlockHandler")
    @Cached(name = "hello", cacheNullValue = true)
    public String hello() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Hello, Sentinel!";
    }
    
    // 熔断降级示例
    @GetMapping("/user/{id}")
    @SentinelResource(value = "getUser", 
                     blockHandler = "getUserBlockHandler",
                     fallback = "getUserFallback")
    public String getUser(@PathVariable Long id) {
        if (id == 0) {
            throw new RuntimeException("无效的用户ID");
        }
        return "User: " + id;
    }
    
    // hello接口的流量控制处理方法
    public String helloBlockHandler(BlockException e) {
        return "请求过于频繁，请稍后再试！";
    }
    
    // getUser接口的流量控制处理方法
    public String getUserBlockHandler(Long id, BlockException e) {
        return "获取用户信息请求过于频繁，请稍后再试！";
    }
    
    // getUser接口的熔断降级处理方法
    public String getUserFallback(Long id, Throwable e) {
        return "降级结果: 获取用户信息失败：" + e.getMessage();
    }
}
