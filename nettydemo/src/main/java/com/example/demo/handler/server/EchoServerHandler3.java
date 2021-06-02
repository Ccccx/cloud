package com.example.demo.handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-21 11:49
 */
@Slf4j
@ChannelHandler.Sharable
public class EchoServerHandler3 extends ChannelInboundHandlerAdapter {



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final ByteBuf byteBuf = (ByteBuf) msg;
        log.info("服务端3收到消息： {}",   byteBuf.toString(CharsetUtil.UTF_8));
        ctx.write(byteBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ChannelHandler channelHandler = this;
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener((ChannelFutureListener) future -> {
            log.info("移除服务端3");
            ctx.pipeline().remove(channelHandler);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       log.error("服务端3发生异常，只做打印：{}", cause.getMessage());
       super.exceptionCaught(ctx, cause);
    }
}
