package com.nan.netty.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket, List<Object> out)
            throws Exception {
        //得到DatagramPacket数据内容
        ByteBuf data = datagramPacket.content();
        //获取分隔符位置
        int i = data.indexOf(0, data.readableBytes() - 1, LogEvent.SEPARATOR);
        if (i < 0) {
            //没找到分隔符，脏数据或者空行
            return;
        }
        //分隔符前面是文件内容
        String filename = data.slice(0, i).toString(CharsetUtil.UTF_8);
        //分隔符后面是实际消息数据
        String logMsg = data.slice(i + 1, data.readableBytes() - i - 1).toString(CharsetUtil.UTF_8);
        //创建LogEvent实例
        LogEvent event = new LogEvent(datagramPacket.sender(), System.currentTimeMillis(), filename, logMsg);
        out.add(event);
    }
}