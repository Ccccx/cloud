package com.example.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-22 16:22
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength < 0) {
            throw new IllegalArgumentException("frameLength must be positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
       while (in.readableBytes() >= frameLength) {
           ByteBuf byteBuf = in.readBytes(frameLength);
           out.add(byteBuf);
       }

    }
}
