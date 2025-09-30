package com.ycc.methodHandle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandleDemo {

    public static void main(String[] args) throws Throwable {
        // 1. 调用实例方法（greet）
        testInvokeInstanceMethod();

        // 2. 调用静态方法（add）
        testInvokeStaticMethod();

        // 3. 调用构造函数创建实例
        testInvokeConstructor();

        // 4. 调用私有方法（secret）
        testInvokePrivateMethod();
    }

    // 1. 调用实例方法
    private static void testInvokeInstanceMethod() throws Throwable {
        // 创建目标实例
        Target target = new Target("Alice");

        // a. 获取Lookup实例（具备访问权限）
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // b. 定义MethodType：返回值String，参数String（greet方法的签名）
        MethodType mt = MethodType.methodType(String.class, String.class);

        // c. 查找实例方法"greet"
        MethodHandle mh = lookup.findVirtual(Target.class, "greet", mt);

        // d. 调用：第一个参数是实例，后续是方法参数
        String result = (String) mh.invoke(target, "Hello");
        System.out.println("实例方法调用结果：" + result); // 输出：Hello, Alice
    }

    // 2. 调用静态方法
    private static void testInvokeStaticMethod() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // 定义MethodType：返回值int，参数int, int（add方法的签名）
        MethodType mt = MethodType.methodType(int.class, int.class, int.class);

        // 查找静态方法"add"
        MethodHandle mh = lookup.findStatic(Target.class, "add", mt);

        // 调用：直接传参数（无实例）
        int sum = (int) mh.invoke(10, 20);
        System.out.println("静态方法调用结果：" + sum); // 输出：30
    }

    // 3. 调用构造函数
    private static void testInvokeConstructor() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // 定义MethodType：返回值void（构造函数返回值为void），参数String（构造函数参数）
        MethodType mt = MethodType.methodType(void.class, String.class);

        // 查找构造函数
        MethodHandle mh = lookup.findConstructor(Target.class, mt);

        // 调用：参数为构造函数的参数，返回实例
        Target newTarget = (Target) mh.invoke("Bob");
        System.out.println("构造函数调用结果：" + newTarget.greet("Hi")); // 输出：Hi, Bob
    }

    // 4. 调用私有方法（需要特殊处理访问权限）
    private static void testInvokePrivateMethod() throws Throwable {
        Target target = new Target("Charlie");

        // 注意：默认lookup无法访问私有方法，需通过目标类内部的lookup或提升权限
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Target.class, MethodHandles.lookup());

        // 定义MethodType：返回值String，参数String（secret方法的签名）
        MethodType mt = MethodType.methodType(String.class, String.class);

        // 查找私有方法"secret"
        MethodHandle mh = lookup.findVirtual(Target.class, "secret", mt);

        // 调用
        String secret = (String) mh.invoke(target, "MethodHandle is powerful!");
        System.out.println("私有方法调用结果：" + secret); // 输出：Secret: MethodHandle is powerful!
    }
}
    