package com.ycc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * 基于 JDK 原生 NIO 的非阻塞 TCP 服务端示例。
 *
 * <p>协议格式（长度前缀，解决粘包/拆包）：
 * <pre>
 *  +-----------+------------------+
 *  | 4 bytes   |  Length bytes    |
 *  |  length   |  UTF-8 payload   |
 *  +-----------+------------------+
 * </pre>
 *
 * <p>工作流程：
 * <ol>
 *   <li>ServerSocketChannel 注册到 Selector，监听 OP_ACCEPT 事件。</li>
 *   <li>新连接到来 → 将 SocketChannel 注册到同一 Selector，监听 OP_READ 事件。</li>
 *   <li>可读事件 → 读取完整报文（先读 4 字节长度头，再读 payload）。</li>
 *   <li>解析成功后打印消息，并将 Echo 写回客户端（同样使用长度前缀协议）。</li>
 * </ol>
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 2.0.0
 */
public class NioServer {

    private static final int PORT = 9090;

    public static void main(String[] args) throws Exception {
        // 打开 Selector（多路复用器）
        Selector selector = Selector.open();

        // 创建服务端 Channel，设置为非阻塞模式
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(PORT));

        // 将服务端 Channel 注册到 Selector，只关心 OP_ACCEPT 事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("[Server] NIO Server started on port " + PORT);

        while (true) {
            // 阻塞直到至少一个 Channel 就绪（超时 3 秒，避免永久阻塞）
            int readyCount = selector.select(3000);
            if (readyCount == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 必须手动移除，否则下次 select 仍会包含该 key
                iterator.remove();

                try {
                    if (key.isAcceptable()) {
                        handleAccept(key, selector);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                } catch (IOException e) {
                    System.err.println("[Server] Channel error, closing: " + e.getMessage());
                    key.cancel();
                    key.channel().close();
                }
            }
        }
    }

    /**
     * 处理新连接：将客户端 SocketChannel 注册到 Selector。
     * <p>为每个连接分配一个读缓冲区，附加到 SelectionKey 的 attachment 中。
     */
    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        if (clientChannel == null) {
            return;
        }
        clientChannel.configureBlocking(false);

        // attachment 存储该连接的读缓冲区（初始 1024 字节，会动态扩展）
        ByteBuffer readBuf = ByteBuffer.allocate(1024);
        clientChannel.register(selector, SelectionKey.OP_READ, readBuf);

        System.out.println("[Server] New connection from: " + clientChannel.getRemoteAddress());
    }

    /**
     * 处理可读事件：按长度前缀协议读取完整报文，然后发送 Echo 回复。
     *
     * <p>粘包/拆包处理逻辑：
     * <ul>
     *   <li>先确保至少读到 4 字节（长度头）。</li>
     *   <li>再确保读到 length 字节的 payload。</li>
     *   <li>若数据不足，保留缓冲区等待下次可读事件。</li>
     * </ul>
     */
    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();

        int bytesRead = channel.read(buf);
        if (bytesRead == -1) {
            // 对端正常关闭
            System.out.println("[Server] Client disconnected: " + channel.getRemoteAddress());
            key.cancel();
            channel.close();
            return;
        }

        // 切换为读模式，尝试解析一条完整消息（可能需要多次可读事件才能凑齐）
        buf.flip();

        // 循环解析，一次 read 可能包含多条消息（粘包场景）
        while (buf.remaining() >= 4) {
            // 标记当前 position，不足时可回退
            buf.mark();
            int msgLen = buf.getInt(); // 读取 4 字节长度头

            if (buf.remaining() < msgLen) {
                // payload 还没收全，回退到 mark 处等待下次读事件
                buf.reset();
                break;
            }

            // 读取 payload
            byte[] payload = new byte[msgLen];
            buf.get(payload);
            String message = new String(payload, StandardCharsets.UTF_8);
            System.out.println("[Server] Received from " + channel.getRemoteAddress() + ": " + message);

            // 发送 Echo 回复
            String reply = "Echo: " + message;
            writeMessage(channel, reply);
        }

        // 将未消费的数据移到缓冲区头部，切换回写模式继续接收
        buf.compact();

        // 若缓冲区已满（说明单条消息超过缓冲区大小），动态扩容
        if (!buf.hasRemaining()) {
            ByteBuffer larger = ByteBuffer.allocate(buf.capacity() * 2);
            buf.flip();
            larger.put(buf);
            key.attach(larger);
        }
    }

    /**
     * 按长度前缀协议将字符串写回客户端。
     * <p>写操作使用同步写（循环确保全部字节写出），生产环境可改为注册 OP_WRITE 异步写。
     *
     * @param channel 目标 SocketChannel
     * @param message 要发送的字符串
     */
    private static void writeMessage(SocketChannel channel, String message) throws IOException {
        byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + payload.length);
        buf.putInt(payload.length);
        buf.put(payload);
        buf.flip();
        // 确保所有字节都写出（SocketChannel 非阻塞模式下 write 可能只写了一部分）
        while (buf.hasRemaining()) {
            channel.write(buf);
        }
    }
}
