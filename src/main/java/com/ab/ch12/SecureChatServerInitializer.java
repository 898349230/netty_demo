package com.ab.ch12;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * @description: 扩展ChatServerInitializer，添加加密
 * @author: sunxinbo
 * @date: 2022/9/29 8:58
 * @param: null
 * @return: null
 **/
public class SecureChatServerInitializer extends ChatServerInitializer {

    private final SslContext context;

    public SecureChatServerInitializer(ChannelGroup group, SslContext context) {
        super(group);
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        SSLEngine engine = context.newEngine(ch.alloc());
        engine.setUseClientMode(false);
//        将 SslHandler 添加到 ChannelPipeline中
        ch.pipeline().addFirst(new SslHandler(engine));
    }
}
