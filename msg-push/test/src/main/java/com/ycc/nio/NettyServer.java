package com.ycc.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class NettyServer {

    public static void main(String[] args) throws Exception {
        // 创建两个 EventLoopGroup 实例，一个用于接收连接，一个用于处理连接
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 用于监听端口
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 用于处理已连接的客户端

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 使用 NIO 传输
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 为每个新连接添加处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new StringDecoder(),
                                    new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                            System.out.println("Received message: " + msg);
                                            ctx.writeAndFlush("Echo: " + msg); // 回复客户端
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            cause.printStackTrace();
                                            ctx.close(); // 出现异常时关闭连接
                                        }
                                    });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置服务端连接队列大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 设置保持连接

            // 绑定端口并启动服务端
            ChannelFuture future = bootstrap.bind(8080).sync();
            System.out.println("Server started and listening on port 8080");

            // 等待服务器关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
