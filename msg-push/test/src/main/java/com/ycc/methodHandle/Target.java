package com.ycc.methodHandle;

/**
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */ // 测试类：包含各种方法供MethodHandle调用
class Target {
    private String name;

    // 实例方法
    public String greet(String prefix) {
        return prefix + ", " + name;
    }

    // 静态方法
    public static int add(int a, int b) {
        return a + b;
    }

    // 构造函数
    public Target(String name) {
        this.name = name;
    }

    // 私有方法
    private String secret(String msg) {
        return "Secret: " + msg;
    }
}
