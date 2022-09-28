package com.ab.ch11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;

public class HttpCompressInitializer extends ChannelInitializer<Channel> {

    private boolean isClient;

    public HttpCompressInitializer(boolean isClient){
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(isClient){
            pipeline.addLast("codec", new HttpClientCodec());
//            http 解压缩
            pipeline.addLast("deCompress", new HttpContentDecompressor());
        }else {
            pipeline.addLast("codec", new HttpClientCodec());
//            http 压缩
            pipeline.addLast("compress", new HttpContentCompressor());
        }
    }
}
