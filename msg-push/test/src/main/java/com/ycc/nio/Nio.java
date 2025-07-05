package com.ycc.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Nio {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(1);
        String apple = "apple";
        String substring = apple.substring(0, 1);
        System.out.println(apple.endsWith("apple"));
    }
}
