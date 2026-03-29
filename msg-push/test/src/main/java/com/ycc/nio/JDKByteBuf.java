package com.ycc.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * JDK NIO ByteBuffer 常见使用示例
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
public class JDKByteBuf {
    public static void main(String[] args) throws IOException {
        // 1. 创建 ByteBuffer
        createExamples();
        
        // 2. 基本读写操作
        readWriteExamples();
        
        // 3. 模式切换 (读/写模式)
        modeSwitchExamples();
        
        // 4. 直接缓冲区
        directBufferExamples();
        
        // 5. 内存映射文件
        mappedFileExamples();
        
        // 6. 分散读取与聚集写入
        scatterGatherExamples();
        
        // 7. 与 Charset 配合使用
        charsetExamples();
    }
    
    /**
     * 1. 创建 ByteBuffer 的多种方式
     */
    private static void createExamples() {
        System.out.println("=== 创建 ByteBuffer 示例 ===");
        
        // 堆内存 ByteBuffer (最常用)
        ByteBuffer heapBuffer = ByteBuffer.allocate(1024);
        System.out.println("堆内存 buffer: " + heapBuffer.getClass().getSimpleName());
        System.out.println("  容量：" + heapBuffer.capacity());
        
        // 直接内存 ByteBuffer (适合 I/O 操作，避免拷贝)
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
        System.out.println("直接内存 buffer: " + directBuffer.getClass().getSimpleName());
        System.out.println("  容量：" + directBuffer.capacity());
        
        // 包装现有数组
        byte[] array = new byte[]{1, 2, 3, 4, 5};
        ByteBuffer wrappedBuffer = ByteBuffer.wrap(array);
        System.out.println("包装数组 buffer: " + wrappedBuffer.capacity() + " bytes");
        System.out.println("  数组偏移量：" + wrappedBuffer.arrayOffset());
        
        // 只读视图
        ByteBuffer readOnlyBuffer = heapBuffer.asReadOnlyBuffer();
        System.out.println("只读视图 buffer: " + readOnlyBuffer.isReadOnly());
    }
    
    /**
     * 2. 基本读写操作示例
     */
    private static void readWriteExamples() {
        System.out.println("\n=== 基本读写操作示例 ===");
        
        ByteBuffer buffer = ByteBuffer.allocate(64);
        
        // 写入基本类型
        buffer.put((byte) 10);
        buffer.putShort((short) 1000);
        buffer.putInt(100000);
        buffer.putLong(10000000000L);
        buffer.putChar('A');
        buffer.putFloat(3.14f);
        buffer.putDouble(3.1415926);
        
        // 写入字节数组
        byte[] data = new byte[]{1, 2, 3, 4, 5};
        buffer.put(data);
        
        // 写入字符串字节
        String str = "Hello NIO";
        buffer.put(str.getBytes(StandardCharsets.UTF_8));
        
        System.out.println("写入后 position: " + buffer.position());
        System.out.println("写入后 limit: " + buffer.limit());
        System.out.println("写入后 capacity: " + buffer.capacity());
        
        // 切换到读模式
        buffer.flip();
        
        // 读取基本类型
        byte b = buffer.get();
        short s = buffer.getShort();
        int i = buffer.getInt();
        long l = buffer.getLong();
        char c = buffer.getChar();
        float f = buffer.getFloat();
        double d = buffer.getDouble();
        
        System.out.println("读取的 byte: " + b);
        System.out.println("读取的 short: " + s);
        System.out.println("读取的 int: " + i);
        System.out.println("读取的 char: " + c);
        
        // 读取到数组
        byte[] readData = new byte[5];
        buffer.get(readData);
        System.out.println("读取的数组：" + java.util.Arrays.toString(readData));
        
        // 读取剩余字节
        byte[] remaining = new byte[buffer.remaining()];
        buffer.get(remaining);
        System.out.println("读取的字符串：" + new String(remaining, StandardCharsets.UTF_8));
    }
    
    /**
     * 3. 模式切换示例 (关键！)
     */
    private static void modeSwitchExamples() {
        System.out.println("\n=== 模式切换示例 ===");
        
        ByteBuffer buffer = ByteBuffer.allocate(32);
        
        // 写模式：position=0, limit=capacity
        System.out.println("初始状态 - position: " + buffer.position() + ", limit: " + buffer.limit());
        
        // 写入数据
        buffer.putInt(123);
        System.out.println("写入后 - position: " + buffer.position() + ", limit: " + buffer.limit());
        
        // flip(): 切换到读模式 (limit=position, position=0)
        buffer.flip();
        System.out.println("flip 后 - position: " + buffer.position() + ", limit: " + buffer.limit());
        
        // 读取部分数据
        buffer.getInt();
        System.out.println("读取后 - position: " + buffer.position() + ", remaining: " + buffer.remaining());
        
        // compact(): 压缩 (删除已读数据，保留未读数据)
        buffer.compact();
        System.out.println("compact 后 - position: " + buffer.position() + ", limit: " + buffer.limit());
        
        // clear(): 清空 (position=0, limit=capacity)，数据仍在但会被覆盖
        buffer.clear();
        System.out.println("clear 后 - position: " + buffer.position() + ", limit: " + buffer.limit());
        
        // rewind(): 重绕 (position=0, limit 不变)，用于重新读取或写入
        buffer.putInt(456);
        buffer.rewind();
        System.out.println("rewind 后 - position: " + buffer.position() + ", limit: " + buffer.limit());
        buffer.putInt(456);
        System.out.println("rewind + putInt 后 - position: " + buffer.position() + ", limit: " + buffer.limit());
    }
    
    /**
     * 4. 直接缓冲区示例
     */
    private static void directBufferExamples() {
        System.out.println("\n=== 直接缓冲区示例 ===");
        
        // 直接缓冲区分配在 JVM 堆外内存
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
        
        System.out.println("直接缓冲区类型：" + directBuffer.getClass().getSimpleName());
        System.out.println("是否为直接缓冲区：" + directBuffer.isDirect());
        System.out.println("容量：" + directBuffer.capacity());
        
        // 直接缓冲区适合：
        // 1. 频繁 I/O 操作 (避免堆内到堆外拷贝)
        // 2. 大数据传输
        // 3. 长期存活的缓冲区
        
        // 注意：直接缓冲区分配和回收成本较高，不适合频繁创建销毁
        
        directBuffer.putInt(12345);
        directBuffer.flip();
        System.out.println("读取的值：" + directBuffer.getInt());
    }
    
    /**
     * 5. 内存映射文件示例
     */
    private static void mappedFileExamples() throws IOException {
        System.out.println("\n=== 内存映射文件示例 ===");
        
        String tempFile = System.getProperty("java.io.tmpdir") + "nio_test.tmp";
        
        // 写入数据 - 使用 RandomAccessFile 支持读写模式
        try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(tempFile, "rw");
             FileChannel channel = raf.getChannel()) {
            
            // 映射文件到内存 (可写)
            MappedByteBuffer mappedBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024);
            
            System.out.println("映射缓冲区类型：" + mappedBuffer.getClass().getSimpleName());
            System.out.println("是否为直接缓冲区：" + mappedBuffer.isDirect());
            
            // 写入数据
            mappedBuffer.put("Hello Mapped File!".getBytes(StandardCharsets.UTF_8));
            mappedBuffer.putInt(9999);
            
            System.out.println("数据已写入映射文件");
        }
        
        // 读取数据
        try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(tempFile, "r");
             FileChannel channel = raf.getChannel()) {
            
            MappedByteBuffer mappedBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, 1024);
            
            // 读取字符串
            byte[] bytes = new byte[20];
            mappedBuffer.get(bytes);
            System.out.println("读取的字符串：" + new String(bytes, StandardCharsets.UTF_8).trim());
            
            // 读取整数
            int value = mappedBuffer.getInt();
            System.out.println("读取的整数：" + value);
        }
        
        // 清理临时文件
        new File(tempFile).delete();
    }
    
    /**
     * 6. 分散读取与聚集写入示例
     */
    private static void scatterGatherExamples() throws IOException {
        System.out.println("\n=== 分散读取与聚集写入示例 ===");
        
        String tempFile = System.getProperty("java.io.tmpdir") + "nio_scatter_gather.tmp";
        
        // 聚集写入 (多个 buffer 写入一个通道)
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             FileChannel channel = fos.getChannel()) {
            
            ByteBuffer header = ByteBuffer.allocate(10);
            ByteBuffer body = ByteBuffer.allocate(30);
            
            // 写入数据 (确保不超过 buffer 容量)
            header.put("HEADER:".getBytes(StandardCharsets.UTF_8));
            body.put("BODY CONTENT HERE".getBytes(StandardCharsets.UTF_8));
            
            header.flip();
            body.flip();
            
            // 聚集写入
            channel.write(new ByteBuffer[]{header, body});
            System.out.println("聚集写入完成");
        }
        
        // 分散读取 (从一个通道读取到多个 buffer)
        try (FileInputStream fis = new FileInputStream(tempFile);
             FileChannel channel = fis.getChannel()) {
            
            ByteBuffer header = ByteBuffer.allocate(10);
            ByteBuffer body = ByteBuffer.allocate(30);
            
            // 分散读取
            channel.read(new ByteBuffer[]{header, body});
            
            header.flip();
            body.flip();
            
            byte[] headerBytes = new byte[header.remaining()];
            byte[] bodyBytes = new byte[body.remaining()];
            
            header.get(headerBytes);
            body.get(bodyBytes);
            
            System.out.println("分散读取 - Header: " + new String(headerBytes, StandardCharsets.UTF_8));
            System.out.println("分散读取 - Body: " + new String(bodyBytes, StandardCharsets.UTF_8));
        }
        
        new File(tempFile).delete();
    }
    
    /**
     * 7. 与 Charset 配合使用示例
     */
    private static void charsetExamples() {
        System.out.println("\n=== Charset 配合使用示例 ===");
        
        ByteBuffer buffer = ByteBuffer.allocate(64);
        
        // 编码：String -> ByteBuffer
        String original = "你好，NIO！Hello!";
        byte[] bytes = original.getBytes(StandardCharsets.UTF_8);
        buffer.put(bytes);
        buffer.flip();
        
        // 解码：ByteBuffer -> String
        byte[] readBytes = new byte[buffer.remaining()];
        buffer.get(readBytes);
        String decoded = new String(readBytes, StandardCharsets.UTF_8);
        System.out.println("UTF-8 解码：" + decoded);
        
        // 使用 CharBuffer 进行编码转换
        buffer.clear();
        CharBuffer charBuffer = CharBuffer.allocate(64);
        charBuffer.put("Charset 测试");
        charBuffer.flip();  // 切换到读模式，准备编码
        
        // 编码为 ByteBuffer (encode 会读取 CharBuffer)
        ByteBuffer encoded = StandardCharsets.UTF_8.encode(charBuffer);
        System.out.println("编码后字节数：" + encoded.remaining());
        
        // 解码回 CharBuffer (decode 读取 ByteBuffer，返回可读的 CharBuffer)
        CharBuffer decodedCharBuffer = StandardCharsets.UTF_8.decode(encoded);
        System.out.println("解码后字符串：" + decodedCharBuffer.toString());
    }
}
