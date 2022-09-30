package com.ab.ch13;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * @description: 广播文件的每一行内容
 * @author: sunxinbo
 * @date: 2022/9/30 23:18
 * @param: null
 * @return: null
 **/
public class LogEventBroadcaster {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
//        引导该 NioDatagramChannel（无连接的）
        bootstrap.group(group).channel(NioDatagramChannel.class)
//                设置 SO_BROADCAST套接字选项
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws InterruptedException, IOException {
//        绑定Channel
        Channel ch = bootstrap.bind(0).sync().channel();
        long pointer = 0;
//        主动主处理循环
        for (; ; ) {
            long length = file.length();
            if (length < pointer) {
//                如果有必要，将文件指针设置到改文件的最后一个字节
                pointer = length;
            } else if (length > pointer) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
//                设置当前文件指针，以确保没有任何的旧日志被发送
                raf.seek(pointer);
                String line;
                while ((line = raf.readLine()) != null) {
//                    对于每个日志条目，写入一个LogEvent到Channel中
                    ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
//                存储其在文件中的当前位置
                pointer = raf.getFilePointer();
                raf.close();
            }
            try {
//                休息一秒，如果被中断，则退出循环，否则重新处理它
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }

        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        int port = 8088;
//        String filePath = Thread.currentThread().getContextClassLoader().getResource("log.log").getPath();
        String filePath = "C:\\Users\\sun\\Desktop\\log.log";
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", port), new File(filePath));
        try {
            broadcaster.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            broadcaster.stop();
        }
    }
}
