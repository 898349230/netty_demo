package com.ab.ch11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * @description: 使用 ChannelInitializer 安装解码器，
 * 协议规范：
 *   传入数据流是一系列的帧，每个帧都由换行符（ \n）分隔；
 * 每个帧都由一系列的元素组成，每个元素都由单个空格字符分隔；
 * 一个帧的内容代表一个命令，定义为一个命令名称后跟着数目可变的参数。我们用于这个协议的自定义解码器将定义以下类：
 * Cmd—将帧（命令）的内容存储在 ByteBuf 中，一个 ByteBuf 用于名称，另一个用于参数；
 * CmdDecoder—从被重写了的 decode()方法中获取一行字符串，并从它的内容构建一个 Cmd 的实例；
 * CmdHandler—从 CmdDecoder 获取解码的 Cmd 对象，并对它进行一些处理；
 * CmdHandlerInitializer —为了简便起见，我们将会把前面的这些类定义为专门的 ChannelInitializer 的嵌套类，
 * 其将会把这些 ChannelInboundHandler 安装到 ChannelPipeline 中
 * @author: sunxinbo
 * @date: 2022/9/27 22:53
 * @param: null
 * @return: null
 **/
public class CmdHandlerInitializer extends ChannelInitializer<Channel> {

    final byte SPACE = (byte) ' ';

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        添加 CmdDecoder 以提取Cmd 对象，并将它转发给下一个ChannelInboundHandler
        pipeline.addLast(new CmdDecoder(64 * 1024));
//        添加 CmdHandler 以接收和处理 Cmd 对象
        pipeline.addLast(new CmdHandler());
    }

    public static final class Cmd {
        private final ByteBuf name;
        private final ByteBuf args;

        public Cmd(ByteBuf name, ByteBuf args) {
            this.name = name;
            this.args = args;
        }

        public ByteBuf name() {
            return name;
        }

        public ByteBuf args() {
            return args;
        }
    }

    public static final class CmdDecoder extends LineBasedFrameDecoder {
        public CmdDecoder(int maxLength) {
            super(maxLength);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
//            从 ByteBuf 中提取由行尾符序列分隔的帧
            ByteBuf frame = (ByteBuf) super.decode(ctx, buffer);
//            从 ByteBuf 中提取由行尾符序列分隔的
            if (frame == null) {
                return null;
            }
//            从 ByteBuf 中提取由行尾符序列分隔的
            int index = frame.indexOf(frame.readerIndex(), frame.writerIndex(), (byte) ' ');
//            使用包含有命令名称和参数的切片创建新的Cmd 对象
            return new Cmd(frame.slice(frame.readerIndex(), index),
                    frame.slice(index + 1, frame.writerIndex()));
        }
    }

    public static final class CmdHandler
            extends SimpleChannelInboundHandler<Cmd> {
        //        处理传经 ChannelPipeline的 Cmd 对象
        @Override
        public void channelRead0(ChannelHandlerContext ctx, Cmd msg)
                throws Exception {
// Do something with the command
        }
    }
}
