package com.example;

import com.example.handler.FixedLengthFrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-22 16:26
 */
public class FixedLengthFrameDecoderTest {

    @Test
    public void t1() {
        final ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }

        final ByteBuf input = buf.duplicate();

        final EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        // 写入数据
        assertTrue(channel.writeInbound(input.retain()));
        // 标记channel为已完成状态
        assertTrue(channel.finish());
        ByteBuf read = (ByteBuf) channel.readInbound();
        // 读取所生成的信息，验证是否为3帧（切片），其中每帧（切片）为3字节
        assertEquals(buf.readSlice(3), read);
        read.release();
        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();
        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();
        // 读取三次后数据为空
        assertNull(channel.readInbound());
        buf.release();

    }

    @Test
    public void t2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3));
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(7)));
        assertTrue(channel.finish());
        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();
        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();
        read = (ByteBuf) channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();
        assertNull(channel.readInbound());
        buf.release();
    }

}
