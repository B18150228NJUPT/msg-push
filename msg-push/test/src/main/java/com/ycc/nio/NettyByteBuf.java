package com.ycc.nio;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Netty ByteBuf 常见使用示例
 */
public class NettyByteBuf {
    public static void main(String[] args) {
        // 1. 创建 ByteBuf
        createExamples();
        
        // 2. 写入数据
        writeExamples();
        
        // 3. 读取数据
        readExamples();
        
        // 4. 容量管理
        capacityExamples();
        
        // 5. 派生缓冲区
        derivedBufferExamples();
        
        // 6. 内存管理
        memoryManagementExamples();
        
        // 7. 工具类使用
        utilityExamples();
    }
    
    /**
     * 1. 创建 ByteBuf 的多种方式
     */
    private static void createExamples() {
        System.out.println("=== 创建 ByteBuf 示例 ===");
        
        // 堆内存 ByteBuf (可扩容)
        io.netty.buffer.ByteBuf heapBuffer = Unpooled.buffer();
        System.out.println("堆内存 buffer: " + heapBuffer.getClass().getSimpleName());
        
        // 直接内存 ByteBuf (性能更好，适合 I/O 操作)
        io.netty.buffer.ByteBuf directBuffer = Unpooled.directBuffer();
        System.out.println("直接内存 buffer: " + directBuffer.getClass().getSimpleName());
        
        // 固定容量的 buffer
        io.netty.buffer.ByteBuf fixedBuffer = Unpooled.buffer(1024);
        System.out.println("固定容量 buffer 初始容量：" + fixedBuffer.capacity());
        
        // 从现有数组创建
        byte[] array = new byte[]{1, 2, 3, 4, 5};
        io.netty.buffer.ByteBuf arrayBuffer = Unpooled.wrappedBuffer(array);
        System.out.println("数组包装 buffer: " + arrayBuffer.readableBytes() + " bytes");
        
        // 从 ByteBuffer 创建
        ByteBuffer nioBuffer = ByteBuffer.allocate(100);
        io.netty.buffer.ByteBuf nioBufferWrapper = Unpooled.wrappedBuffer(nioBuffer);
        System.out.println("ByteBuffer 包装：" + nioBufferWrapper.capacity() + " bytes");
        
        // 复制创建
        io.netty.buffer.ByteBuf copiedBuffer = Unpooled.copiedBuffer("Hello Netty", StandardCharsets.UTF_8);
        System.out.println("复制创建：" + copiedBuffer.toString(StandardCharsets.UTF_8));
        
        // 清理
        heapBuffer.release();
        directBuffer.release();
        fixedBuffer.release();
        arrayBuffer.release();
        nioBufferWrapper.release();
        copiedBuffer.release();
    }
    
    /**
     * 2. 写入数据示例
     */
    private static void writeExamples() {
        System.out.println("\n=== 写入数据示例 ===");
        
        io.netty.buffer.ByteBuf buffer = Unpooled.buffer(100);
        
        // 写入基本类型
        buffer.writeByte(10);
        buffer.writeShort(1000);
        buffer.writeInt(100000);
        buffer.writeLong(10000000000L);
        
        // 写入字节数组
        byte[] data = new byte[]{1, 2, 3, 4, 5};
        buffer.writeBytes(data);
        
        // 写入字符串
        String str = "Hello World";
        buffer.writeBytes(str.getBytes(StandardCharsets.UTF_8));
        
        // 写入另一个 ByteBuf
        io.netty.buffer.ByteBuf srcBuffer = Unpooled.buffer();
        srcBuffer.writeBytes(new byte[]{10, 20, 30});
        buffer.writeBytes(srcBuffer);
        
        System.out.println("写入后容量：" + buffer.capacity());
        System.out.println("可读字节数：" + buffer.readableBytes());
        System.out.println("可写字节数：" + buffer.writableBytes());
        
        buffer.release();
        srcBuffer.release();
    }
    
    /**
     * 3. 读取数据示例
     */
    private static void readExamples() {
        System.out.println("\n=== 读取数据示例 ===");
        
        io.netty.buffer.ByteBuf buffer = Unpooled.buffer(100);
        buffer.writeInt(12345);
        buffer.writeBytes("测试数据".getBytes(StandardCharsets.UTF_8));
        
        // 记录读取起始位置
        buffer.markReaderIndex();
        
        // 读取基本类型
        int value = buffer.readInt();
        System.out.println("读取的 int 值：" + value);
        
        // 读取字节数组
        byte[] bytes = new byte[8];
        buffer.readBytes(bytes);
        System.out.println("读取的字符串：" + new String(bytes, StandardCharsets.UTF_8));
        
        // 跳过指定字节数
        buffer.skipBytes(2);
        
        // 恢复到标记位置
        buffer.resetReaderIndex();
        System.out.println("重置后 readerIndex: " + buffer.readerIndex());
        
        // 获取 ByteBuffer (可用于 NIO 通道读写)
        ByteBuffer nioBuf = buffer.nioBuffer();
        System.out.println("获取的 ByteBuffer 剩余：" + nioBuf.remaining());
        
        buffer.release();
    }
    
    /**
     * 4. 容量管理示例
     */
    private static void capacityExamples() {
        System.out.println("\n=== 容量管理示例 ===");
        
        // 自动扩容
        io.netty.buffer.ByteBuf autoExpand = Unpooled.buffer(10);
        System.out.println("初始容量：" + autoExpand.capacity());
        byte[] largeData = new byte[100];
        autoExpand.writeBytes(largeData);
        System.out.println("写入 100 字节后容量：" + autoExpand.capacity());
        
        // 手动调整容量
        io.netty.buffer.ByteBuf manualExpand = Unpooled.buffer(50);
        manualExpand.capacity(200);
        System.out.println("手动调整容量后：" + manualExpand.capacity());
        
        // 确保可写字节数
        io.netty.buffer.ByteBuf ensureWritable = Unpooled.buffer(10);
        System.out.println("当前可写字节：" + ensureWritable.writableBytes());
        ensureWritable.ensureWritable(100);
        System.out.println("ensureWritable(100) 后可写字节：" + ensureWritable.writableBytes());
        
        autoExpand.release();
        manualExpand.release();
        ensureWritable.release();
    }
    
    /**
     * 5. 派生缓冲区示例
     */
    private static void derivedBufferExamples() {
        System.out.println("\n=== 派生缓冲区示例 ===");
        
        io.netty.buffer.ByteBuf original = Unpooled.buffer(100);
        original.writeBytes("Hello World".getBytes(StandardCharsets.UTF_8));
        
        // 切片 (共享内容，独立的读写索引)
        io.netty.buffer.ByteBuf slice = original.slice(0, 5);
        System.out.println("切片内容：" + slice.toString(StandardCharsets.UTF_8));
        
        // 只读视图
        io.netty.buffer.ByteBuf readOnly = original.asReadOnly();
        System.out.println("是否为只读：" + readOnly.isReadOnly());
        
        // duplicate (共享内容和索引)
        io.netty.buffer.ByteBuf duplicate = original.duplicate();
        System.out.println("duplicate 的 readerIndex: " + duplicate.readerIndex());
        original.readerIndex(3);
        System.out.println("修改 original 后 duplicate 的 readerIndex: " + duplicate.readerIndex());
        
        original.release();
    }
    
    /**
     * 6. 内存管理示例
     */
    private static void memoryManagementExamples() {
        System.out.println("\n=== 内存管理示例 ===");
        
        // 引用计数
        io.netty.buffer.ByteBuf buffer = Unpooled.buffer();
        System.out.println("初始引用计数：" + buffer.refCnt());
        
        // 增加引用
        buffer.retain();
        System.out.println("retain 后引用计数：" + buffer.refCnt());
        
        // 减少引用
        buffer.release();
        System.out.println("release 后引用计数：" + buffer.refCnt());
        
        // 最终释放
        buffer.release();
        System.out.println("最终释放后引用计数：" + buffer.refCnt());
        
        // CompositeByteBuf (零拷贝合并)
        CompositeByteBuf composite = Unpooled.compositeBuffer();
        io.netty.buffer.ByteBuf buf1 = Unpooled.copiedBuffer("Part1", StandardCharsets.UTF_8);
        io.netty.buffer.ByteBuf buf2 = Unpooled.copiedBuffer("Part2", StandardCharsets.UTF_8);
        composite.addComponents(true, buf1, buf2);
        System.out.println("CompositeBuf 内容：" + composite.toString(StandardCharsets.UTF_8));
        composite.release();
    }
    
    /**
     * 7. 工具类使用示例
     */
    private static void utilityExamples() {
        System.out.println("\n=== 工具类使用示例 ===");
        
        io.netty.buffer.ByteBuf buf1 = Unpooled.copiedBuffer("Hello", StandardCharsets.UTF_8);
        io.netty.buffer.ByteBuf buf2 = Unpooled.copiedBuffer("World", StandardCharsets.UTF_8);
        
        // 比较两个 ByteBuf
        int cmp = ByteBufUtil.compare(buf1, buf2);
        System.out.println("ByteBuf 比较结果：" + cmp);
        
        // 计算 hashCode
        int hash = ByteBufUtil.hashCode(buf1);
        System.out.println("ByteBuf hashCode: " + hash);
        
        // 转换为字节数组
        byte[] bytes = ByteBufUtil.getBytes(buf1);
        System.out.println("转换为字节数组长度：" + bytes.length);
        
        // 获取十六进制字符串
        String hex = ByteBufUtil.hexDump(buf1);
        System.out.println("十六进制表示：" + hex);
        
        // 打印 ASCII 图
        String pretty = ByteBufUtil.prettyHexDump(buf1);
        System.out.println("格式化十六进制:\n" + pretty);
        
        buf1.release();
        buf2.release();
    }
}
