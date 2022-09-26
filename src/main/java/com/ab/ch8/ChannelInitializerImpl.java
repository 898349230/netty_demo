package com.ab.ch8;

import com.ab.ch1.client.EchoClientHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * @description: 将所需的ChannelHandler添加到ChannelPipeline
 * @author: sunxinbo
 * @date: 2022/9/24 12:16
 * @param: null
 * @return: null
 **/
public class ChannelInitializerImpl extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new EchoClientHandler());
    }
}
