package com.ycc.nio;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * UDP 组播接收端
 *
 * <p>原理说明：
 * MulticastSocket 是 DatagramSocket 的子类，用于收发 UDP 组播数据报。
 * 组播地址范围：224.0.0.0 ~ 239.255.255.255（D 类 IP 地址）
 *   - 224.0.0.x  本地链路组播（路由器不转发）
 *   - 239.x.x.x  管理范围组播（局域网内使用，本例采用此段）
 *
 * 工作流程：
 *   1. 创建 MulticastSocket 并绑定到组播端口
 *   2. 调用 joinGroup() 向操作系统声明"我想接收发往该组的数据包"
 *      ——OS 会通知网卡接受目标 IP 为组播地址的数据帧
 *   3. 循环调用 receive() 阻塞等待数据报
 *   4. 退出前调用 leaveGroup() 取消组播组成员身份
 *
 * 运行方式：先启动 MulticastReceiver，再启动 MulticastSender
 *
 * @author ycc
 */
public class MulticastReceiver {

    /** 组播地址（D 类 IP，239.x.x.x 为本地管理范围） */
    private static final String MULTICAST_GROUP = "239.8.8.8";

    /** 组播端口，发送端与接收端必须一致 */
    private static final int    MULTICAST_PORT  = 9527;

    /** 接收缓冲区大小（字节） */
    private static final int    BUFFER_SIZE     = 1024;

    /** receive() 超时时间（毫秒），超时后检查是否应退出循环 */
    private static final int    SO_TIMEOUT_MS   = 3000;

    public static void main(String[] args) throws Exception {

        InetAddress group = InetAddress.getByName(MULTICAST_GROUP);

        // 1. 创建 MulticastSocket 并绑定到组播端口
        //    注意：绑定端口而非绑定具体 IP，这样才能接收发往组播地址的数据报
        try (MulticastSocket socket = new MulticastSocket(MULTICAST_PORT)) {

            // 设置接收超时，避免 receive() 永久阻塞
            socket.setSoTimeout(SO_TIMEOUT_MS);

            // 设置接收自己发送的组播包（true = 本机也能收到自己发的包，便于单机测试）
            socket.setLoopbackMode(false); // false 表示启用本地回环

            // 2. 加入组播组
            //    joinGroup(InetAddress) 在所有网络接口上加入，适用于简单场景
            //    若有多块网卡，可用 joinGroup(SocketAddress, NetworkInterface) 精确指定
            socket.joinGroup(group);
            System.out.println("[Receiver] 已加入组播组 " + MULTICAST_GROUP + ":" + MULTICAST_PORT);
            System.out.println("[Receiver] 等待组播消息（超过 " + SO_TIMEOUT_MS / 1000 + "s 无消息则自动退出）...");

            byte[] buf = new byte[BUFFER_SIZE];
            int consecutiveTimeouts = 0;

            // 3. 循环接收组播数据报
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                    consecutiveTimeouts = 0; // 重置超时计数

                    String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    System.out.printf("[Receiver] 收到来自 %s:%d 的消息：%s%n",
                            packet.getAddress().getHostAddress(),
                            packet.getPort(),
                            message);

                    // 收到 "quit" 信号时退出
                    if ("quit".equalsIgnoreCase(message.trim())) {
                        System.out.println("[Receiver] 收到退出信号，准备关闭。");
                        break;
                    }

                } catch (SocketTimeoutException e) {
                    consecutiveTimeouts++;
                    System.out.println("[Receiver] 超时等待中... (第 " + consecutiveTimeouts + " 次)");
                    // 连续 5 次超时（共 15 秒）无数据则退出，防止永久挂起
                    if (consecutiveTimeouts >= 5) {
                        System.out.println("[Receiver] 长时间无消息，自动退出。");
                        break;
                    }
                }
            }

            // 4. 离开组播组（可选，try-with-resources 关闭 socket 时 OS 也会自动处理）
            socket.leaveGroup(group);
            System.out.println("[Receiver] 已离开组播组，接收端退出。");
        }
    }
}
