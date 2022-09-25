package com.ab.ch9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength){
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        检查是否有足够的字节可以读取，以生成下一帧
        while(in.readableBytes() >= frameLength){
//            从 ByteBuf 中读取一个帧
            ByteBuf byteBuf = in.readBytes(frameLength);
//            将该帧添加到已被解码的消息列表中
            out.add(byteBuf);
        }
    }
}