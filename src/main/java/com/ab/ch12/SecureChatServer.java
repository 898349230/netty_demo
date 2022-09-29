package com.ab.ch12;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * @description: ssl加密的
 * @author: sunxinbo
 * @date: 2022/9/29 9:08
 * @param: null
 * @return: null
 **/
public class SecureChatServer extends ChatServer {

    private final SslContext context;

    public SecureChatServer(SslContext context) {
        this.context = context;
    }

    @Override
    protected ChannelHandler createInitializer(ChannelGroup channelGroup) {
        return new SecureChatServerInitializer(channelGroup, context);
    }

    public static void main(String[] args) throws CertificateException, SSLException {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContext context = SslContext.newServerContext(
                cert.certificate(), cert.privateKey());
        final SecureChatServer server = new SecureChatServer(context);
        ChannelFuture future = server.start(new InetSocketAddress(8088));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.destory()));
        future.channel().closeFuture().syncUninterruptibly();
    }
}
