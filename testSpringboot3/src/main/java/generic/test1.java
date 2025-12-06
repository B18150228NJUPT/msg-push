package generic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
public class test1 {

    // 定义接口
    interface Service {
        void performAction();
    }

    // 实现类
    static class RealService implements Service {
        public void performAction() {
            System.out.println("执行实际操作");
        }
    }

    // 调用处理器
    static class MyInvocationHandler implements InvocationHandler {
        private Object target;

        public MyInvocationHandler(Object target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            System.out.println("前置处理");
            Object result = method.invoke(target, args);
            System.out.println("后置处理");
            return result;
        }
    }

    public static void main(String[] args) {
        // 创建代理
        Service realService = new RealService();
        Service proxy = (Service) Proxy.newProxyInstance(
                Service.class.getClassLoader(),
                new Class[]{Service.class},
                new MyInvocationHandler(realService)
        );
        proxy.performAction();
    }
}
