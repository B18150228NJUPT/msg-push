package com.ycc.nio;

import java.nio.ByteBuffer;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
public class JDKByteBuf1 {
    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        // 切换读模式
        allocate.flip();
        System.out.println(allocate.remaining());
        // 位置重置0，写模块，覆盖之前的数据
        allocate.clear();
        System.out.println(allocate.remaining());
    }
}
