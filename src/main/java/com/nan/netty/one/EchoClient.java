package com.nan.netty.one;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建客户端启动器实例
            Bootstrap b = new Bootstrap();
            //使用NioEventLoopGroup接收事件，和服务端一样，这里使用NIO传输
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    //指定ChannelHandler，一旦连接简历就会创建Channel
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //指定EchoClientHandler处理业务逻辑
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            //同步连接服务端
            ChannelFuture f = b.connect().sync();
            //阻塞代码只到客户端关闭
            f.channel().closeFuture().sync();
        } finally {
            //关闭启动器并释放资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new EchoClient("localhost", 9999).start();
    }
}