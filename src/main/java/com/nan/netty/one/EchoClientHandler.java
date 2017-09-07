package com.nan.netty.one;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

//不同Channel之间共享此Handler
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //连接成功后向服务端发送数据
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello server 110", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //打印从服务端收到的数据，然后关闭连接
        System.out.println("Received from server: " + msg.getCharSequence(0, msg.readableBytes(), CharsetUtil.UTF_8));
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //打印遇到的异常，然后关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
