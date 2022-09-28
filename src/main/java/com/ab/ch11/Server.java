package com.ab.ch11;

import com.ab.ch1.server.EchoServer;
import com.ab.ch1.server.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class Server {
    private int port;
    public Server (int port){
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new Server(8080).start();
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
//                    .childHandler(new HttpPipelineInitializer(false));
                    .childHandler(new HttpAggregatorInitializer(false));
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
