package com.ab.ch9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        检查是否有足够的字节用来编码
        while(in.readableBytes() >= 4){
//            该整数写入到编码消息的List中
            int i = in.readInt();
            int value = Math.abs(i);
            System.out.println("AbsIntegerEncoder : i = " + i + " value = " + value);
            out.add(value);
        }
    }
}
