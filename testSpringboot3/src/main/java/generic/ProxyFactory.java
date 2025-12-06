package generic;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class ProxyFactory {
    public static <T> T createProxy(Class<T> targetClass, MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        // 设置父类（目标类）
        enhancer.setSuperclass(targetClass);
        // 设置回调函数（拦截器）
        enhancer.setCallback(interceptor);
        // 创建代理对象
        return (T) enhancer.create();
    }

}