package com.ab.ch1.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private int port;
    public EchoServer (int port){
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoServer(10089).start();
    }

    public void start() throws InterruptedException {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
//        用于进行事件的处理，如接收新链接以及读写数据
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
//            用于引导和绑定服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(nioEventLoopGroup)
                    // 指定使用NIO传出channel
                    .channel(NioServerSocketChannel.class)
//                    指定的端口设置套接字
                    .localAddress(new InetSocketAddress(port))
//                    添加 handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(echoServerHandler);
                        }
                    });
//            异步绑定服务器，调用 sync 方法阻塞等待直到绑定完成
            ChannelFuture future = bootstrap.bind().sync();
//            获取 channel 的 CloseFuture 并且阻塞当前线程直到它完成
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
//            关闭 EventLoopGroup，释放所有资源
            nioEventLoopGroup.shutdownGracefully().sync();
        }

    }
}
