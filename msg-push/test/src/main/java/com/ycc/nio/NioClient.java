package com.ycc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * 基于 JDK 原生 NIO 的非阻塞 TCP 客户端示例。
 *
 * <p>与 {@link NioServer} 共用同一套长度前缀协议：
 * <pre>
 *  +-----------+------------------+
 *  | 4 bytes   |  Length bytes    |
 *  |  length   |  UTF-8 payload   |
 *  +-----------+------------------+
 * </pre>
 *
 * <p>工作流程：
 * <ol>
 *   <li>以非阻塞方式发起 TCP 连接，注册 OP_CONNECT 事件。</li>
 *   <li>OP_CONNECT 就绪后调用 {@code finishConnect()}，注册 OP_READ，并发送首条消息。</li>
 *   <li>OP_READ 就绪后读取服务端 Echo，按长度前缀解析完整消息并打印。</li>
 *   <li>收到 Echo 后再发送第二条消息，演示多轮交互。</li>
 *   <li>完成所有交互后优雅关闭。</li>
 * </ol>
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 2.0.0
 */
public class NioClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9090;
    /** 最多等待服务端回复的轮次，演示多轮交互 */
    private static final int MAX_ROUNDS = 3;

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();

        // 创建 SocketChannel，设置为非阻塞
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);

        // 发起异步连接（立即返回，不阻塞）
        boolean connected = channel.connect(new InetSocketAddress(HOST, PORT));

        if (connected) {
            // 极少数情况：本地回环地址可能立即完成连接
            System.out.println("[Client] Connected immediately to " + HOST + ":" + PORT);
            channel.register(selector, SelectionKey.OP_READ, newState());
            sendMessage(channel, "Hello NIO Server! (round 1)");
        } else {
            // 正常路径：注册 OP_CONNECT，等待连接完成通知
            channel.register(selector, SelectionKey.OP_CONNECT, newState());
        }

        System.out.println("[Client] NIO Client started, connecting to " + HOST + ":" + PORT);

        // 事件循环
        while (true) {
            int readyCount = selector.select(3000);
            if (readyCount == 0) {
                // 超时后检查是否还有待处理的 key
                if (selector.keys().isEmpty()) {
                    break;
                }
                continue;
            }

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    if (key.isConnectable()) {
                        handleConnect(key);
                    } else if (key.isReadable()) {
                        boolean done = handleRead(key);
                        if (done) {
                            // 已完成全部交互，退出循环
                            key.cancel();
                            key.channel().close();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("[Client] Error: " + e.getMessage());
                    key.cancel();
                    key.channel().close();
                }
            }

            // 若所有 key 都已取消，退出主循环
            if (selector.keys().isEmpty()) {
                break;
            }
        }

        selector.close();
        System.out.println("[Client] Client shut down gracefully.");
    }

    /**
     * 处理 OP_CONNECT 事件：完成三次握手，注册 OP_READ 并发送首条消息。
     */
    private static void handleConnect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.finishConnect()) {
            System.out.println("[Client] Connected to " + HOST + ":" + PORT);
            // 连接成功后转为关注读事件
            key.interestOps(SelectionKey.OP_READ);
            // 发送第一条消息
            sendMessage(channel, "Hello NIO Server! (round 1)");
        }
    }

    /**
     * 处理 OP_READ 事件：按长度前缀协议读取并解析服务端回复。
     *
     * @return {@code true} 表示已完成全部交互，可以关闭连接
     */
    private static boolean handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        ByteBuffer buf = state.readBuf;

        int bytesRead = channel.read(buf);
        if (bytesRead == -1) {
            System.out.println("[Client] Server closed the connection.");
            return true;
        }

        buf.flip();

        while (buf.remaining() >= 4) {
            buf.mark();
            int msgLen = buf.getInt();

            if (buf.remaining() < msgLen) {
                buf.reset();
                break;
            }

            byte[] payload = new byte[msgLen];
            buf.get(payload);
            String reply = new String(payload, StandardCharsets.UTF_8);
            System.out.println("[Client] Received: " + reply);

            state.roundsReceived++;
            if (state.roundsReceived < MAX_ROUNDS) {
                // 继续发送下一条消息
                sendMessage(channel, "Hello NIO Server! (round " + (state.roundsReceived + 1) + ")");
            } else {
                System.out.println("[Client] All " + MAX_ROUNDS + " rounds completed.");
                buf.compact();
                return true;
            }
        }

        buf.compact();
        return false;
    }

    /**
     * 按长度前缀协议发送字符串消息。
     *
     * @param channel 目标 SocketChannel
     * @param message 要发送的消息
     */
    private static void sendMessage(SocketChannel channel, String message) throws IOException {
        byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + payload.length);
        buf.putInt(payload.length);
        buf.put(payload);
        buf.flip();
        while (buf.hasRemaining()) {
            channel.write(buf);
        }
        System.out.println("[Client] Sent: " + message);
    }

    /** 创建每个连接独享的状态对象 */
    private static ClientState newState() {
        return new ClientState();
    }

    /**
     * 每条连接的私有状态，存储在 SelectionKey 的 attachment 中。
     */
    private static class ClientState {
        /** 读缓冲区，初始 1024 字节 */
        final ByteBuffer readBuf = ByteBuffer.allocate(1024);
        /** 已完成的交互轮次（已收到的回复数） */
        int roundsReceived = 0;
    }
}
