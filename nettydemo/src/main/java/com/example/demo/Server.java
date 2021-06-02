package com.example.demo;

import com.example.demo.handler.server.EchoServerHandler;
import com.example.demo.handler.server.EchoServerHandlerEnd;
import com.example.demo.handler.server.TestExceptionServerHandler;
import com.example.demo.handler.server.TestExceptionServerHandler1;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-21 11:39
 */
@Slf4j
public class Server {


    public static void main(String[] args)  {
        final ServerBootstrap bootstrap = new ServerBootstrap();

        final  ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

        final EventLoopGroup boos = new NioEventLoopGroup();
        final NioEventLoopGroup work = new NioEventLoopGroup(4);
        try {
        bootstrap.group(boos, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(18080)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addFirst("echo", new EchoServerHandler());
                        channel.pipeline().addLast("end", new EchoServerHandlerEnd(channelGroup));
                        channel.pipeline().addLast(new TestExceptionServerHandler1());
                        channel.pipeline().addLast(new TestExceptionServerHandler());
                    }
                });

        log.info("服务端启动完成 ...");
        final ChannelFuture   future = bootstrap.bind().sync();
        future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
           close(boos);
           close(work);
        }

    }

    private static void close(EventLoopGroup group) {
        try {
            group.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
