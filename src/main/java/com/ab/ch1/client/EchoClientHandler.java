package com.ab.ch1.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 被通知 channel 是活跃时发送一条消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(System.currentTimeMillis() + " channelActive...");
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty Rock", CharsetUtil.UTF_8));
//        super.channelActive(ctx);
    }

    /**
     * 每次接收数据都会调用这个方法
     * 当 channelRead0 方法完成时已经有了传入消息，并且已经处理完了，该方法返回时，
     * SimpleChannelInboundHandler 负责释放指向保存该BYteBuff的内存引用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println(System.currentTimeMillis() + " channelRead0... client received " + msg.toString(CharsetUtil.UTF_8));
    }

    /**
     * @description:
     * @author: sunxinbo
     * @date: 2022/9/17 11:06
     * @param: ctx
     * @param: cause
     **/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
//        super.exceptionCaught(ctx, cause);
    }
}
