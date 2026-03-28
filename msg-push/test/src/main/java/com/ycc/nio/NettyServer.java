package com.ycc.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 基于 Netty 的 TCP 服务端示例。
 *
 * <p>Pipeline 处理链：
 * <pre>
 *  网络字节流
 *      ↓
 *  StringDecoder  （ByteBuf → String，使用 UTF-8）
 *      ↓
 *  StringEncoder  （String → ByteBuf，使用 UTF-8）
 *      ↓
 *  ServerHandler  （业务逻辑：打印消息、Echo 回复）
 * </pre>
 *
 * <p><b>注意</b>：StringDecoder/StringEncoder 不处理粘包/拆包，生产环境应在其前
 * 增加 {@code LengthFieldBasedFrameDecoder} / {@code LengthFieldPrepender}
 * 或 {@code DelimiterBasedFrameDecoder} 等帧解码器。
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 2.0.0
 */
public class NettyServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        // bossGroup：只负责接受新连接（单线程足够）
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // workerGroup：负责已建立连接的 I/O 读写（默认线程数 = CPU核数 × 2）
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 使用 NIO 传输层
                    .channel(NioServerSocketChannel.class)
                    // 服务端接受队列大小（TCP backlog）
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 对每个新连接的 SocketChannel 配置 Pipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    // Inbound：ByteBuf → String（UTF-8 解码）
                                    .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                                    // Outbound：String → ByteBuf（UTF-8 编码）
                                    // 必须添加此编码器，否则 writeAndFlush(String) 无法正常工作
                                    .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                                    // 业务处理器
                                    .addLast("handler", new ServerHandler());
                        }
                    })
                    // 对子 Channel（客户端连接）开启 TCP 心跳探测
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口并同步等待绑定成功
            ChannelFuture future = bootstrap.bind(PORT).sync();
            System.out.println("[NettyServer] Server started on port " + PORT);

            // 阻塞直到服务端 Channel 关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭两个线程组，释放所有资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("[NettyServer] Server shut down gracefully.");
        }
    }

    /**
     * 服务端业务处理器（非线程共享，每个连接独占一个实例）。
     *
     * <p>继承 {@link SimpleChannelInboundHandler} 后，Netty 会在 {@code channelRead0}
     * 执行完毕后自动释放 {@code msg} 的引用计数，无需手动调用 {@code ReferenceCountUtil.release()}。
     */
    private static class ServerHandler extends SimpleChannelInboundHandler<String> {

        /** 客户端连接建立时触发 */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println("[NettyServer] Client connected: " + ctx.channel().remoteAddress());
        }

        /** 客户端连接断开时触发 */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            System.out.println("[NettyServer] Client disconnected: " + ctx.channel().remoteAddress());
        }

        /**
         * 收到客户端消息时触发。
         *
         * @param ctx 通道上下文，可用于写回数据
         * @param msg 已由 StringDecoder 解码后的字符串
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            System.out.println("[NettyServer] Received from "
                    + ctx.channel().remoteAddress() + ": " + msg);
            // Echo 回复：StringEncoder 会将字符串自动编码为 ByteBuf
            ctx.writeAndFlush("Echo: " + msg);
        }

        /** 发生异常时关闭连接，避免资源泄漏 */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.err.println("[NettyServer] Exception from "
                    + ctx.channel().remoteAddress() + ": " + cause.getMessage());
            ctx.close();
        }
    }
}
