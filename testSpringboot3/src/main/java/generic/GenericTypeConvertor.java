package generic;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
@Component
public class GenericTypeConvertor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Order) {
            Object proxyInstance = Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    bean.getClass().getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            System.out.println("args" + Arrays.toString(args));
                            Object result = method.invoke(bean, args);
                            return result;
                        }
                    }
            );
            System.out.println(proxyInstance);

            // 2. 原始目标对象
            // 3. 构建 Spring JDK 代理（通过 ProxyFactory 简化配置）
            org.springframework.aop.framework.ProxyFactory proxyFactory = new org.springframework.aop.framework.ProxyFactory();
            proxyFactory.setTarget(bean); // 绑定原始对象（类似 TargetHolder 持有目标）
            proxyFactory.setInterfaces(CallBack.class); // 明确代理接口（JDK 代理必需）
            proxyFactory.setOptimize(true);

            // 4. 添加代理增强（可选：如方法执行前后日志）
            NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
            pointcut.addMethodName("call"); // 仅增强 sayHello 方法
            proxyFactory.addAdvisor(new DefaultPointcutAdvisor(
                    pointcut,
                    new org.aopalliance.intercept.MethodInterceptor() {
                        @Override
                        public Object invoke(MethodInvocation i) throws Throwable {
                            System.out.println("method "+i.getMethod()+" is called on "+
                                    i.getThis()+" with args "+i.getArguments());
                            Object ret=i.proceed();
                            System.out.println("method "+i.getMethod()+" returns "+ret);
                            return ret;
                        }
                    }

//            (method, args1, target1, chain) -> { // 增强逻辑（MethodInterceptor）
//                        System.out.println("Spring 代理增强：方法执行前");
//                        Object result = chain.proceed(); // 转发到原始对象
//                        System.out.println("Spring 代理增强：方法执行后");
//                        return result;
//                    }
//            )

            ));

            // 5. 生成代理对象
            CallBack proxy = (CallBack) proxyFactory.getProxy();

            // 7. 获取原始对象（核心：通过 Advised 接口，无需反射！）
            if (proxy instanceof Advised) {
                Advised advised = (Advised) proxy;
                Object originalTarget = null;
                try {
                    originalTarget = advised.getTargetSource().getTarget();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return originalTarget;
            }

            return proxyInstance;
        }
        return bean;
    }

    public static void main(String[] args) {
        CallBackImpl callBack = new CallBackImpl();

        // 1. jdk proxy
        CallBack proxyInstance = (CallBack) Proxy.newProxyInstance(
                CallBack.class.getClassLoader(),
                new Class[]{CallBack.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("args" + Arrays.toString(args));
                        Object result = method.invoke(callBack, args);
                        return result;
                    }
                }
        );
        ArrayList<User> users = new ArrayList<>();
        users.add(new User());
        proxyInstance.call(new Order(), users);

        // get CallBackImpl interface type
        Type genericSuperclass = callBack.getClass().getGenericInterfaces()[0];
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericSuperclass;
            Type[] actualTypes = paramType.getActualTypeArguments();
            // 获取 K 和 V 的具体类型
        }

        // 2. cglib proxy
        CallBack proxy = ProxyFactory.createProxy(CallBack.class, new MethodInterceptor() {

            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("args" + Arrays.toString(objects));
                Object result = method.invoke(callBack, objects);
                System.out.println("result" + result);
                return result;
            }
        });
        // get CallBackImpl interface type
        genericSuperclass = proxy.getClass().getGenericInterfaces()[0];
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericSuperclass;
            Type[] actualTypes = paramType.getActualTypeArguments();
            // 获取 K 和 V 的具体类型
        }

    }



}


