package com.ab.ch9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

public class EmbeddedChannelTest {
    public static void main(String[] args) {

//        test1();
//        test2();
        test3();

    }

    private static void test3() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 10; i++) {
            buffer.writeInt(i * -1);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
//        写入出站数据
        System.out.println(channel.writeOutbound(buffer));
//        标记该Channel为已完成状态
        System.out.println(channel.finish());

//        依次读取 AbsIntegerEncoder 中 encoder 过的数据
        for (int i = 0; i < 10; i++) {
            System.out.println(channel.readOutbound().toString());
        }
    }

    private static void test2() {
        //        创建 ByteBuf ，并且存储9个字节
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();
//        创建EmbeddedChannel，并且添加一个 FixLengthFrameDecoder，已3字节的帧长度被测试
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
//        返回 false， 因为没有一个可以完整的可供读取的帧
        System.out.println(channel.writeInbound(input.readBytes(2)));
//        返回true
        System.out.println(channel.writeInbound(input.readBytes(7)));
//        标记为已完成状态
        System.out.println("channel.finish(): " + channel.finish());
//      每次读取三个字节
        ByteBuf read = channel.readInbound();
        System.out.println("buffer.readSlice(3).equals(read) :" + (buffer.readSlice(3).equals(read)));
        read.release();

        read = channel.readInbound();
        System.out.println("buffer.readSlice(3).equals(read):" + (buffer.readSlice(3).equals(read)));
        read.release();

        read = channel.readInbound();
        System.out.println("buffer.readSlice(3).equals(read):" + (buffer.readSlice(3).equals(read)));
        read.release();

//        最后为 null， 读取过了 9 个
        System.out.println("channel.readInbound() " + channel.readInbound() );
        buffer.release();
    }

    private static void test1(){
        //        创建 ByteBuf ，并且存储9个字节
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();
//        创建EmbeddedChannel，并且添加一个 FixLengthFrameDecoder，已3字节的帧长度被测试
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        System.out.println("channel.writeInbound: " + channel.writeInbound(input.retain()));
//        标记为已完成状态
        System.out.println("channel.finish(): " + channel.finish());
//      每次读取三个字节
        ByteBuf read = channel.readInbound();
        System.out.println("buffer.readSlice(3).equals(read) :" + (buffer.readSlice(3).equals(read)));
        read.release();

        read = channel.readInbound();
        System.out.println("buffer.readSlice(3).equals(read):" + (buffer.readSlice(3).equals(read)));
        read.release();

        read = channel.readInbound();
        System.out.println("buffer.readSlice(3).equals(read):" + (buffer.readSlice(3).equals(read)));
        read.release();

//        最后为 null， 读取过了 9 个
        System.out.println("channel.readInbound() " + channel.readInbound() );
        buffer.release();
    }
}
