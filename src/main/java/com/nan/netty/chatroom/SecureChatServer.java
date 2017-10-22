package com.nan.netty.chatroom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class SecureChatServer extends ChatServer {

    private final SSLContext context;

    public SecureChatServer(SSLContext context) {
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
        //Channel初始化使用安全版本的
        return new SecureChatServerInitializer(group, context);

    }

    public static void main(String[] args) {
        int port = 9999;
        SSLContext sslContext = null;
        //读取SSL证书文件，配置SSLContext
        try (InputStream ksInputStream = new FileInputStream(SecureChatServer.class.getResource("/").getPath() + "../resources/netty.jks")) {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(ksInputStream, "netty123".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, "netty123".toCharArray());
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final SecureChatServer endpoint = new SecureChatServer(sslContext);
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> endpoint.destroy()));
        future.channel().closeFuture().syncUninterruptibly();
    }
}