package com.nan.netty.memcached;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class MemcachedResponseDecoder extends ByteToMessageDecoder {

    private enum State {
        Header,
        Body
    }

    private State state = State.Header;
    private int totalBodySize;
    private byte magic;
    private byte opCode;
    private short keyLength;
    private byte extraLength;
    private byte dataType;
    private short status;
    private int id;
    private long cas;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        switch (state) {
            case Header:
                if (in.readableBytes() < 24) {
                    //收到的字节长度小于24，可能是错误信息，就不解析了
                    return;
                }
                //读取所有头信息
                magic = in.readByte();
                opCode = in.readByte();
                keyLength = in.readShort();
                extraLength = in.readByte();
                dataType = in.readByte();
                status = in.readShort();
                totalBodySize = in.readInt();
                id = in.readInt();
                cas = in.readLong();
                state = State.Body;
                //解析完头信息，开始解析数据体
            case Body:
                if (in.readableBytes() < totalBodySize) {
                    //收到的数据不完整
                    return;
                }
                int flags = 0, expires = 0;
                int actualBodySize = totalBodySize;
                if (extraLength > 0) {
                    flags = in.readInt();
                    actualBodySize -= 4;
                }
                if (extraLength > 4) {
                    expires = in.readInt();
                    actualBodySize -= 4;
                }
                String key = "";
                if (keyLength > 0) {
                    ByteBuf keyBytes = in.readBytes(keyLength);
                    key = keyBytes.toString(CharsetUtil.UTF_8);
                    actualBodySize -= keyLength;
                }
                ByteBuf body = in.readBytes(actualBodySize);
                String data = body.toString(CharsetUtil.UTF_8);
                out.add(new MemcachedResponse(
                        magic,
                        opCode,
                        dataType,
                        status,
                        id,
                        cas,
                        flags,
                        expires,
                        key,
                        data
                ));
                state = State.Header;
        }
    }
}