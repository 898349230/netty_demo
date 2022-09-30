package com.ab.ch13;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private final InetSocketAddress remoteAddress;

    //    LogEventEncoder 创建了即将被发送到指定的InetSocketAddress 的DatagramPacket 消息
    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent logEvent, List<Object> out) throws Exception {
        byte[] file = logEvent.getLogfile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer(file.length + msg.length + 1);
//        写入数据
        buffer.writeBytes(file);
        buffer.writeByte(LogEvent.SEPARATOR);
        buffer.writeBytes(msg);
//        将一个拥有数据和目的地地址的新 DatagramPacket添加到出站的消息列表中
        out.add(new DatagramPacket(buffer, remoteAddress));
    }
}
