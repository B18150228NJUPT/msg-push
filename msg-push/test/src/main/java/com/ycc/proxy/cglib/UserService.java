package com.ycc.proxy.cglib;

public class UserService {
    public String createUser(String name) {
        System.out.println("创建用户: " + name);
        deleteUser("1");
        return "用户ID: " + System.currentTimeMillis();
    }

    public void deleteUser(String id) {
        System.out.println("删除用户: " + id);
    }
}