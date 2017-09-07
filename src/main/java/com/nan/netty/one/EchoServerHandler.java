package com.nan.netty.one;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

//Sharable注解可以让此Handler在不同Channel之间共享
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //Netty的ByteBuf转成字符串
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("Server received: " + byteBuf.getCharSequence(0, byteBuf.readableBytes(), Charset.forName("UTF-8")));
        //收到数据并将数据发送到客户端，但是此时数据还没有刷到对等连接点
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //读操作完成后，将所有写入的数据(等待中的)刷新到对等连接点，然后关闭此Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        //出现异常关闭此Channel
        ctx.close();
    }
}
