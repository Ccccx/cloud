package com.example.demo.handler.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-21 11:49
 */
@Slf4j
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        long i = System.currentTimeMillis();
        String handelName;
//        if (i % 2 ==0) {
//            handelName = "echo2";
//            ctx.pipeline().addAfter("echo", "echo2", new EchoServerHandler2());
//        } else {
//            handelName = "echo3";
//            ctx.pipeline().addAfter("echo", "echo3", new EchoServerHandler3());
//        }
//        log.info("模拟Handler 切换 {} ...", handelName);
        TimeUnit.SECONDS.sleep(2);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       log.error("发生异常，只做打印：{}", cause.getMessage());
       super.exceptionCaught(ctx, cause);
    }
}
