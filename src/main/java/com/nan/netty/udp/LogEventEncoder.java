package com.nan.netty.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private final InetSocketAddress remoteAddress;

    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LogEvent logEvent, List<Object> out)
            throws Exception {
        ByteBuf buf = channelHandlerContext.alloc().buffer();
        //文件名称写到ByteBuf中
        buf.writeBytes(logEvent.getLogfile().getBytes(CharsetUtil.UTF_8));
        //分割字符
        buf.writeByte(LogEvent.SEPARATOR);
        //实际文件内容
        buf.writeBytes(logEvent.getMsg().getBytes(CharsetUtil.UTF_8));
        //创建DatagramPacket实例并添加到结果列表
        out.add(new DatagramPacket(buf, remoteAddress));
    }
}