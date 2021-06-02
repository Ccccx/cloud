package com.example;

import com.example.handler.AbsIntegerEncode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

import static org.junit.Assert.*;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-11-22 17:08
 */
public class AbsIntegerEncodeTest {
    public void t1() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }
        EmbeddedChannel channel = new EmbeddedChannel(
                new AbsIntegerEncode());
        assertTrue(channel.writeOutbound(buf));
        assertTrue(channel.finish());
// read bytes
        for (int i = 1; i < 10; i++) {
            assertEquals(java.util.Optional.of(i), channel.readOutbound());
        }
        assertNull(channel.readOutbound());
    }
}
