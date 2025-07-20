package com.ycc.nio;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/18
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder(),
                                    new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                                            System.out.println("Received message: " + s);
                                        }
                                    }

                            );
                        }
                    });

            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();


            future.channel().writeAndFlush("Hello from client");

            System.out.println("send msg");

//            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }
}
