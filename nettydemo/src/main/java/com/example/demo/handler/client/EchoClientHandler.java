package com.example.demo.handler.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-21 13:37
 */
@Slf4j
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private final boolean send;
    private final String name;

    public EchoClientHandler(boolean send, String name) {
        this.send = send;
        this.name = name;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (send) {
            log.info("5秒后开始发送数据, 并且每5秒执行一次...");
            final Channel channel = ctx.channel();
            channel.eventLoop().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    String dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    ctx.writeAndFlush(Unpooled.copiedBuffer(" 【" + name + "】Netty Time :" + dateTime , CharsetUtil.UTF_8));
                }
            },5 , 5, TimeUnit.SECONDS);
        } else {
            log.info("{} 只发送一次消息 ...", name);
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            ctx.writeAndFlush(Unpooled.copiedBuffer(" 【" + name + "】Netty Time :" + dateTime , CharsetUtil.UTF_8));
        }


    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
       log.info("客户端收到消息：{}", msg.toString(CharsetUtil.UTF_8));
        TimeUnit.SECONDS.sleep(2);

       // final ChannelPipeline pipeline = ctx.pipeline();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("客户端发生异常： ", cause);
        ctx.close();
    }
}
