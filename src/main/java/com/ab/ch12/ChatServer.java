package com.ab.ch12;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

public class ChatServer {

    //    创建 DefaultChannelGroup 将其保存所有已经连接的 WebSocket Channel
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    //    引导服务器
    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup));
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    //    创建 ChatServerInitializer
    protected ChannelHandler createInitializer(ChannelGroup channelGroup) {
        return new ChatServerInitializer(channelGroup);
    }

    //    处理服务器关闭，并释放所有的资源
    public void destory() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws CertificateException, SSLException {
        final ChatServer server = new ChatServer();
        ChannelFuture future = server.start(new InetSocketAddress(8088));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.destory()));
        future.channel().closeFuture().syncUninterruptibly();
    }
}
