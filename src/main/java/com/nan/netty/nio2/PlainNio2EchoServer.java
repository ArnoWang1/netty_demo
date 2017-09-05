package com.nan.netty.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

public class PlainNio2EchoServer {

    public void serve(int port) throws IOException {
        System.out.println("Listening for connections on port " + port);

        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        //服务端绑定端口
        serverChannel.bind(address);
        final CountDownLatch latch = new CountDownLatch(1);
        //开始接收客户端连接，一旦连接进来，参数CompletionHandler将会回调
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(final AsynchronousSocketChannel channel,
                                  Object attachment) {
                //再次接收客户端连接
                serverChannel.accept(null, this);
                ByteBuffer buffer = ByteBuffer.allocate(100);
                //触发读操作，一旦读到内容就会触发EchoCompletionHandler
                channel.read(buffer, buffer, new EchoCompletionHandler(channel));
            }

            @Override
            public void failed(Throwable throwable, Object attachment) {
                try {
                    //出现错误时关闭Socket
                    serverChannel.close();
                } catch (IOException e) {
                } finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private final class EchoCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

        private final AsynchronousSocketChannel channel;

        EchoCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer buffer) {
            String line = Charset.forName("UTF-8").decode(buffer).toString();
            System.out.println("Read from client: " + line);
            buffer.flip();
            //触发写操作，一旦内容写出就会回调CompletionHandler
            channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    if (buffer.hasRemaining()) {
                        //如果ByteBuffer还有内容继续触发写操作
                        channel.write(buffer, buffer, this);
                    } else {
                        buffer.compact();
                        //ByteBuffer内容读完了，重新触发读操作
                        channel.read(buffer, buffer, EchoCompletionHandler.this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                    }
                }
            });
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                channel.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 主执行方法
     */
    public static void main(String[] args) throws IOException {
        new PlainNio2EchoServer().serve(9999);
    }
}
