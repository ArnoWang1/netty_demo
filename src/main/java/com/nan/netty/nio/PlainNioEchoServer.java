package com.nan.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class PlainNioEchoServer {

    public void serve(int port) throws IOException {
        System.out.println("Listening for connections on port " + port);

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        //绑定端口
        ss.bind(address);
        serverChannel.configureBlocking(false);
        Selector selector = Selector.open();
        //Channel注册选择器并指定触发事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            try {
                //阻塞代码只到触发接受客户端连接事件
                selector.select();
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }
            //获取所有的SelectionKey实例
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //移除已经遍历的SelectionKey
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        //接收客户端连接
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        //客户端注册选择器并指定ByteBuffer
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, ByteBuffer.allocate(100));
                    }
                    //检查SelectionKey是否可读
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        //读取数据
                        client.read(output);
                        String line = Charset.forName("UTF-8").decode(output).toString();
                        System.out.println("Read from client: " + line);
                    }
                    //检查SelectionKey是否可写
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        output.flip();
                        //写数据
                        client.write(output);
                        output.compact();
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                    }
                }
            }
        }
    }

    /**
     * 主执行方法
     */
    public static void main(String[] args) throws IOException {
        new PlainNioEchoServer().serve(9999);
    }
}