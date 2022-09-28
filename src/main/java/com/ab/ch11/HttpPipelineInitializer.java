package com.ab.ch11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

/**
 * @Description HTTP 支持
 * @Author sunxinbo
 * @Return
 * @Date 2022/9/25 22:13
 */
public class HttpPipelineInitializer extends ChannelInitializer<Channel> {

    private boolean client;

    public HttpPipelineInitializer(boolean client){
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        是客户端
        if (client){
            pipeline.addLast("httpDecoder", new HttpResponseDecoder());
            pipeline.addLast("httpEncoder", new HttpRequestEncoder());
        } else {
//            是服务器
            pipeline.addLast("httpDecoder", new HttpRequestDecoder());
            pipeline.addLast("httpEncoder", new HttpResponseEncoder());
            pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
            pipeline.addLast("customHttpHandler", new CustomHttpHandler());
        }
    }
}
