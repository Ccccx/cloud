package com.example.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-05 15:33
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    /**
     *  自定义处理事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //   如果该事件 表示握手成 功，则从该 Channelipeline 中移除 HttpRequestHandler， 因为将不会 接收到任何 HTTP 消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 1 通知所有已经连接的 WebSocket 客户端新 的客户端已经连接上了
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + "joined"));
            // 2 将新的 WebSocket Channel 添加到 ChannelGroup 中，以 便它可以接收到所有的消息
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 3 增加消息的引用计数，并将 它写到 ChannelGroup 中所有 已经连接的客户端
        group.writeAndFlush(msg.retain());
    }


}
