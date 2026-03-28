package com.ycc.nio;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

/**
 * UDP 组播发送端
 *
 * <p>原理说明：
 * 发送端通过 MulticastSocket（也可用普通 DatagramSocket）向组播地址发送 UDP 数据报。
 * 网络层会将该数据报复制并路由到所有已加入该组播组的主机——
 * 这正是组播与单播的核心区别：一次发送，多端接收，带宽消耗仅为一份。
 *
 * 关键参数：
 *   - setTimeToLive(ttl)：控制组播数据报可穿越的路由跳数
 *       ttl = 0  只在本机回环接口
 *       ttl = 1  只在本地子网（不穿越路由器，本例使用此值）
 *       ttl > 1  可跨越多个路由器
 *
 * 运行方式：先启动 MulticastReceiver，再启动本类
 *
 * @author ycc
 */
public class MulticastSender {

    /** 与接收端相同的组播地址和端口 */
    private static final String MULTICAST_GROUP = "239.8.8.8";
    private static final int    MULTICAST_PORT  = 9527;

    /** 发送消息的总轮次 */
    private static final int    TOTAL_ROUNDS    = 5;

    /** 每次发送后的间隔（毫秒） */
    private static final long   SEND_INTERVAL   = 1000L;

    public static void main(String[] args) throws Exception {

        InetAddress group = InetAddress.getByName(MULTICAST_GROUP);

        // 发送端使用 MulticastSocket（也可用 DatagramSocket，效果相同）
        // 绑定本地随机端口，不绑定组播端口
        try (MulticastSocket socket = new MulticastSocket()) {

            // TTL = 1：数据报只在本地局域网内传播，不穿越路由器
            socket.setTimeToLive(1);

            // 启用本地回环，使得本机上的接收端也能收到（单机测试必须开启）
            socket.setLoopbackMode(false); // false = 启用回环

            System.out.println("[Sender] 开始向组播组 " + MULTICAST_GROUP + ":" + MULTICAST_PORT + " 发送消息");
            System.out.println("[Sender] TTL=1（仅本地子网），共发送 " + TOTAL_ROUNDS + " 条消息\n");

            for (int i = 1; i <= TOTAL_ROUNDS; i++) {
                String message = "Hello Multicast! 第 " + i + " 条 [来自 " + MULTICAST_GROUP + "]";
                byte[] payload = message.getBytes(StandardCharsets.UTF_8);

                // 构造发往组播地址的 DatagramPacket
                DatagramPacket packet = new DatagramPacket(
                        payload, payload.length,
                        group, MULTICAST_PORT);

                socket.send(packet);
                System.out.println("[Sender] 已发送 (" + i + "/" + TOTAL_ROUNDS + "): " + message);

                Thread.sleep(SEND_INTERVAL);
            }

            // 发送退出信号，通知接收端优雅关闭
            byte[] quitPayload = "quit".getBytes(StandardCharsets.UTF_8);
            DatagramPacket quitPacket = new DatagramPacket(quitPayload, quitPayload.length, group, MULTICAST_PORT);
            socket.send(quitPacket);
            System.out.println("\n[Sender] 已发送退出信号，发送端退出。");
        }
    }
}
