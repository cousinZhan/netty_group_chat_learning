package com.doak.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author ：zhanyiqun
 * @description：服务端启动
 * @date ：Created in 2019/10/18
 */
public class SimpleChatServer {

    private int port;

    public SimpleChatServer(int port) {
        this.port = port;
    }

    public void run() {
        // 创建两个EventLoopGroup，一个用来处理客户端连接，一个用来处理消息
        EventLoopGroup connectGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 服务端引导
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(connectGroup, workerGroup)
                    // 指定IO方式
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SimpleChatServerInitial())
                    // 指定最大连接数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 会向两个小时没有发送过过消息的客户端发送一个活动探测客户端状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("服务端已经启动");
            ChannelFuture future = bootstrap.bind(port).sync();
            // 因为服务端启动后一直会阻塞，实际上这一句代码是不会执行到的
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            connectGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("服务端已经关闭");
        }
    }

    public static void main(String[] args) {
        new SimpleChatServer(8889).run();
    }
}

