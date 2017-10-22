package com.nan.netty.chatroom;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class SecureChatServerInitializer extends ChatServerInitializer {

    private final SSLContext context;

    public SecureChatServerInitializer(ChannelGroup group, SSLContext context) {
        super(group);
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        //父类原样初始化
        super.initChannel(ch);
        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(false);
        //加密解密的SslHandler需要放到首位
        ch.pipeline().addFirst(new SslHandler(engine));
    }
}