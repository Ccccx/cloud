package com.example.websocket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.net.InetSocketAddress;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-05 16:22
 */
public class SecureChatServer extends ChatServer {
    private SslContext context;

    public SecureChatServer(SslContext context) {
        this.context = context;
    }

    @Override
    protected ChannelHandler createInitializer(ChannelGroup channelGroup) {
        return new SecureChatServerInitializer(channelGroup, context);
    }

    public static void main(String[] args) throws Exception {
        final SelfSignedCertificate cert = new SelfSignedCertificate();
        final SslContext context = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).build();

        final SecureChatServer chatServer = new SecureChatServer(context);

        final ChannelFuture future = chatServer.start(new InetSocketAddress(8080));

        Runtime.getRuntime().addShutdownHook(new Thread(chatServer::destroy));

        future.channel().closeFuture().syncUninterruptibly();
    }
}
