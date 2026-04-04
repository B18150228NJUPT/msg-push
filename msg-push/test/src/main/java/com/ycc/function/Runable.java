package com.ycc.function;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
public class Runable {
    public static void main(String[] args) {
        Runnable runable = getRunable();
    }

    private static Runnable getRunable() {
        System.out.printf("invoke what? ");
        return () -> {
            System.out.println("really invoked");
        };
    }
}
