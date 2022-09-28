package com.ab.ch12;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @description: 扩展 SimpleChannelInboundHandler 以处理FullHttpRequest 消息
 * 管理纯粹的 HTTP 请求和响应
 * @author: sunxinbo
 * @date: 2022/9/28 9:04
 * @param: null
 * @return: null
 **/
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private static File INDEX;

    static {
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    /**
     * @description: channelRead0()方法的实现是如何转发任何目标 URI 为/ws 的请求的
     * @author: sunxinbo
     * @date: 2022/9/28 22:35
     * @param: ctx
     * @param: request
     **/
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //如果请求了 WebSocket协议升级，则增加引用计数（调用 retain()方法）并将它传递给下一个ChannelInboundHandler
        if (wsUri.equalsIgnoreCase(request.getUri())) {
//            通过调用 fireChannelRead(msg)方法将它转发给下一个 ChannelInboundHandler
//            之所以需要调用 retain()方法， 是因为调用 channelRead()方法完成之后，它将调用 FullHttpRequest 对象上的 release()方法以释放它的资源
            ctx.fireChannelRead(request.retain());
        } else {
//            处理 100 Continue请求以符合 HTTP1.1 规范
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
//            读取 index.html
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            DefaultHttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
            boolean keepAlive = HttpHeaders.isKeepAlive(request);
//            如果请求了 keep-alive，则添加所需要的HTTP头信息
            if (keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
//            将 HttpResponse写到客户端
            ctx.write(response);
//            检查是否有 SslHandler 存在于在 ChannelPipeline 中
            if (ctx.pipeline().get(SslHandler.class) == null) {
//                将index.html写到客户端
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
//            写 LastHttpContent 并冲刷到客户端， 写一个 LastHttpContent来标记响应的结束
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
//                如果没有keepAlive。则在写操作完成后关闭
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
