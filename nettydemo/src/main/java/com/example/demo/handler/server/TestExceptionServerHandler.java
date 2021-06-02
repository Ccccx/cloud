package com.example.demo.handler.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-21 11:49
 */
@Slf4j
@ChannelHandler.Sharable
public class TestExceptionServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       log.error("发生异常，处理异常：", cause);
       // 可以选择关闭链接
      // ctx.close();
    }
}
