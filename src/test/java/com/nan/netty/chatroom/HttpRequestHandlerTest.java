package com.nan.netty.chatroom;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.junit.Assert;
import org.junit.Test;

public class HttpRequestHandlerTest {

    @Test
    public void testHttpRequest() {
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestHandler("/ws"));
        channel.writeInbound(fullHttpRequest);
        channel.finish();
        Object object = channel.readOutbound();
        Assert.assertTrue(object.getClass().equals(DefaultHttpResponse.class));
    }

}