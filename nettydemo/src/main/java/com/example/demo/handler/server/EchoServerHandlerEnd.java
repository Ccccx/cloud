package com.example.demo.handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-21 11:49
 */
@Slf4j
@ChannelHandler.Sharable
public class EchoServerHandlerEnd extends ChannelInboundHandlerAdapter {

    private final ChannelGroup channelGroup;

    public EchoServerHandlerEnd(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("通道建立 ...");
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final ByteBuf byteBuf = (ByteBuf) msg;
        log.info("END收到消息： {}",   byteBuf.toString(CharsetUtil.UTF_8));
        channelGroup.writeAndFlush(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       log.error("END发生异常，只做打印：{}", cause.getMessage());
       super.exceptionCaught(ctx, cause);
    }
}
