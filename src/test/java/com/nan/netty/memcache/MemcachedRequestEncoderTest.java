package com.nan.netty.memcache;

import com.nan.netty.memcached.MemcachedRequest;
import com.nan.netty.memcached.MemcachedRequestEncoder;
import com.nan.netty.memcached.Opcode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Test;

public class MemcachedRequestEncoderTest {

    @Test
    public void testMemcachedRequestEncoder() {
        //SET指令请求对象
        MemcachedRequest request = new MemcachedRequest(Opcode.SET, "key1", "value1");
        EmbeddedChannel channel = new EmbeddedChannel(new MemcachedRequestEncoder());
        Assert.assertTrue(channel.writeOutbound(request));
        ByteBuf encoded = channel.readOutbound();
        Assert.assertNotNull(encoded);
        //比对协议中的各个属性
        Assert.assertEquals(request.magic(), encoded.readByte());
        Assert.assertEquals(request.opCode(), encoded.readByte());
        Assert.assertEquals(4, encoded.readShort());
        Assert.assertEquals((byte) 0x08, encoded.readByte());
        Assert.assertEquals((byte) 0, encoded.readByte());
        Assert.assertEquals(0, encoded.readShort());
        Assert.assertEquals(4 + 6 + 8, encoded.readInt());
        Assert.assertEquals(request.id(), encoded.readInt());
        Assert.assertEquals(request.cas(), encoded.readLong());
        Assert.assertEquals(request.flags(), encoded.readInt());
        Assert.assertEquals(request.expires(), encoded.readInt());
        byte[] data = new byte[encoded.readableBytes()];
        encoded.readBytes(data);
        Assert.assertArrayEquals((request.key() + request.body()).getBytes(CharsetUtil.UTF_8), data);
        Assert.assertFalse(encoded.isReadable());
        Assert.assertFalse(channel.finish());
        Assert.assertNull(channel.readInbound());
    }
}