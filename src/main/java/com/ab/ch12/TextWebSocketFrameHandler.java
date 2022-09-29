package com.ab.ch12;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup channelGroup;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.channelGroup = group;
    }

    /**
     * @description: 重写userEventTriggered()方法以处理自定义事件
     * @author: sunxinbo
     * @date: 2022/9/28 22:46
     * @param: ctx
     * @param: evt
     **/
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
//            如果该事件表示握手成功，则从该ChannelPipeline中移除HttpRequestHandler，因为将不会接收到任何HTTP请求
            ctx.pipeline().remove(HttpRequestHandler.class);
//            通知所有已经连接的WebSocket客户端新的客户端已经连上了
            channelGroup.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
//            将新的WebSocket Channel 添加到 ChannelGroup中，以便它可以接收到所有的消息
            channelGroup.add(ctx.channel());
            System.out.println("[TextWebSocketFrameHandler] [userEventTriggered]...");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
//      增加消息的引用计数，并将它写到 ChannelGroup中所有已经连接的客户端
//      retain()方法的调用是必需的，因为当 channelRead0()方法返回时.TextWebSocketFrame 的引用计数将会被减少。
//      由于所有的操作都是异步的，因此， writeAndFlush()方法可能会在 channelRead0()方法返回之后完成，而且它绝对不能访问一个已经失效的引用
        System.out.println("[TextWebSocketFrameHandler] [channelRead0]...");
        channelGroup.writeAndFlush(msg.retain());
    }
}
