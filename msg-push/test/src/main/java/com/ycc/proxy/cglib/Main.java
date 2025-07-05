package com.ycc.proxy.cglib;



public class Main {
    public static void main(String[] args) {
     /*   // 创建原始对象实例
        UserService realService = new UserService();

        // 创建代理对象，包装原始对象
        UserService proxy = CglibProxyFactary
//        createProxy(realService);

        // 调用代理方法
        String userId = proxy.createUser("张三");
        proxy.deleteUser(userId);*/


        /*DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        UserService bean = new UserService();
        Object userService = creator.postProcessBeforeInitialization(bean, "userService");
        System.out.println("proxy: " + userService);
        System.out.println("source: " + bean);
        if (userService instanceof UserService) {
            UserService proxy = (UserService) userService;
            String userId = proxy.createUser("张三");
            proxy.deleteUser(userId);
        }*/
    }
}