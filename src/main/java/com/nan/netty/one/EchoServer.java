package com.nan.netty.one;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //服务端启动器
            ServerBootstrap b = new ServerBootstrap();
            //配置服务端使用NIO传输，绑定本地端口
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //Channel增加业务处理类
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            //服务端绑定并等待应用关闭释放资源
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() + " started and listen on" + f.channel().localAddress()); //#7
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * 主执行方法
     */
    public static void main(String[] args) throws Exception {
        new EchoServer(9999).start();
    }
}
