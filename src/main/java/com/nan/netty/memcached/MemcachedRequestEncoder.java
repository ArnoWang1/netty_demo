package com.nan.netty.memcached;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class MemcachedRequestEncoder extends MessageToByteEncoder<MemcachedRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MemcachedRequest msg, ByteBuf out) throws Exception {
        //将键值转为字节
        byte[] key = msg.key().getBytes(CharsetUtil.UTF_8);
        byte[] body = msg.body().getBytes(CharsetUtil.UTF_8);
        //总大小就是键值长度加附加数据长度
        int bodySize = key.length + body.length + (msg.hasExtras() ? 8 : 0);
        //逻辑码序列化
        out.writeByte(msg.magic());
        //操作码序列化
        out.writeByte(msg.opCode());
        //序列化键长度
        out.writeShort(key.length);
        //附加数据长度
        int extraSize = msg.hasExtras() ? 0x08 : 0x0;
        out.writeByte(extraSize);
        //数据类型，由于我们没实现，所以填0
        out.writeByte(0);
        //预留位置，这里我们没有使用，还是写0
        out.writeShort(0);
        //数据大小
        out.writeInt(bodySize);
        //请求标识
        out.writeInt(msg.id());
        //数据版本校验
        out.writeLong(msg.cas());
        if (msg.hasExtras()) {
            //附加数据，标识码和过期
            out.writeInt(msg.flags());
            out.writeInt(msg.expires());
        }
        //键
        out.writeBytes(key);
        //值
        out.writeBytes(body);
    }
}