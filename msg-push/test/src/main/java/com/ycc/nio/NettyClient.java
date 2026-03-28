package com.ycc.nio;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 基于 Netty 的 TCP 客户端示例。
 *
 * <p>Pipeline 处理链：
 * <pre>
 *  网络字节流
 *      ↓
 *  StringDecoder  （ByteBuf → String，使用 UTF-8）
 *      ↓
 *  StringEncoder  （String → ByteBuf，使用 UTF-8）
 *      ↓
 *  ClientHandler  （业务逻辑：连接后发消息、打印服务端回复）
 * </pre>
 *
 * <p><b>注意</b>：StringDecoder/StringEncoder 不处理粘包/拆包，生产环境应在其前
 * 增加 {@code LengthFieldBasedFrameDecoder} / {@code LengthFieldPrepender} 等帧解码器。
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 2.0.0
 */
public class NettyClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;
    /** 收到服务端回复的最大轮次，演示多轮交互 */
    private static final int MAX_ROUNDS = 3;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    // Inbound：ByteBuf → String（UTF-8 解码）
                                    // 必须添加此解码器，否则收到的是 ByteBuf，无法当作 String 处理
                                    .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                                    // Outbound：String → ByteBuf（UTF-8 编码）
                                    .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                                    // 业务处理器
                                    .addLast("handler", new ClientHandler());
                        }
                    });

            // 发起连接并同步等待
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            System.out.println("[NettyClient] Connected to " + HOST + ":" + PORT);

            // 等待连接关闭（由 ClientHandler 在完成所有交互后主动关闭）
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            System.out.println("[NettyClient] Client shut down gracefully.");
        }
    }

    /**
     * 客户端业务处理器。
     *
     * <p>连接建立后立即发送第一条消息；每收到服务端一次 Echo，继续发送下一条，
     * 直到达到 {@link NettyClient#MAX_ROUNDS} 轮后关闭连接。
     */
    @ChannelHandler.Sharable
    private static class ClientHandler extends SimpleChannelInboundHandler<String> {

        private int roundsReceived = 0;

        /** 连接建立时触发：发送第一条消息 */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println("[NettyClient] Channel active, sending first message...");
            ctx.writeAndFlush("Hello Netty Server! (round 1)");
        }

        /** 连接断开时触发 */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            System.out.println("[NettyClient] Channel inactive (disconnected).");
        }

        /**
         * 收到服务端回复时触发。
         *
         * @param ctx 通道上下文
         * @param msg 已由 StringDecoder 解码后的字符串
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            System.out.println("[NettyClient] Received: " + msg);
            roundsReceived++;

            if (roundsReceived < MAX_ROUNDS) {
                // 继续发送下一条消息
                String nextMsg = "Hello Netty Server! (round " + (roundsReceived + 1) + ")";
                ctx.writeAndFlush(nextMsg);
            } else {
                // 完成全部交互，优雅关闭连接
                System.out.println("[NettyClient] All " + MAX_ROUNDS + " rounds completed, closing...");
                ctx.close();
            }
        }

        /** 发生异常时关闭连接，避免资源泄漏 */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.err.println("[NettyClient] Exception: " + cause.getMessage());
            ctx.close();
        }
    }
}
