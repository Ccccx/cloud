package com.example.demo;

import com.example.demo.handler.client.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-21 13:36
 */
@Slf4j
public class Client implements Runnable{


    private final boolean send;
    private final String name;

    public Client(boolean send, String name) {
        this.send = send;
        this.name = name;
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Client(false, "cx")).start();
        new Thread(new Client(false, "ycy")).start();
        new Client(true, "cjz").run();
    }

    public void start(boolean send, String name) throws InterruptedException {

        final NioEventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory(name, Thread.MAX_PRIORITY));
        try {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 18080))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast("echo", new EchoClientHandler(send, name));
                        }
                    });
            log.info("{} 客户端启动完成 ...", name);
            final ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    @Override
    public void run() {
        try {
            start(send, name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
