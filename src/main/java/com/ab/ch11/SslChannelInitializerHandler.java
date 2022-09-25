package com.ab.ch11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * @Description 添加 SSL/TLS 支持
 * @Author sunxinbo
 * @Return
 * @Date 2022/9/25 21:46
 */
public class SslChannelInitializerHandler extends ChannelInitializer<Channel> {

    private final SslContext sslContext;

    private final boolean startTls;

    public SslChannelInitializerHandler(SslContext sslContext, boolean startTls){
        this.sslContext = sslContext;
//        如果设置为 true，第一个写入的消息将不会被加密（客户端应该设置为 true）
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
//对于每个 SslHandler 实例，都使用 Channel 的 ByteBufAllocator 从 SslContext 获取一个新的 SSLEngine
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        ch.pipeline().addFirst("ssl", new SslHandler(sslEngine, startTls));
    }
}
