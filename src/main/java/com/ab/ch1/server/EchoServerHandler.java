package com.ab.ch1.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 一个 handler 可有被多个 channel共享
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 每个传入的消息都会调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf in = (ByteBuf) msg;
        System.out.println(System.currentTimeMillis() + " channelRead... Server received " + in.toString(CharsetUtil.UTF_8));
        // 接受到的消息写给发送者，不冲刷出站消息， write操作是异步的，直到 channelRead 方法返回后可能还没有执行玩
        ctx.write(in);
//        super.channelRead(ctx, msg);
    }

    /**
     * 通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(System.currentTimeMillis() + " channelReadComplete...");
        // 将未决消息冲刷到远程结点，并且关闭该channel, writeAndFlush方法被调用时消息被释放
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//        super.channelReadComplete(ctx);
    }

    /**
     * 在读取操作期间， 有异常抛出时会调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 关闭 channel
        ctx.close();
//        super.exceptionCaught(ctx, cause);
    }
}
