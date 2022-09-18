package com.ab.ch1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

public class SimpleTest {
    public static void main(String[] args) {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        System.out.println((char)buf.readByte());
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        buf.writeByte((byte)'?');
        System.out.println((char)buf.readByte());
        System.out.println(readerIndex == buf.readerIndex());
        System.out.println(writerIndex != buf.writerIndex());
//        assert readerIndex == buf.readerIndex();
// 将会成功，因为 writeByte 修改了writerIndex
//        assert writerIndex != buf.writerIndex();
    }
}
