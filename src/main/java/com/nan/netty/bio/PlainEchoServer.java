package com.nan.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PlainEchoServer {

    public void serve(int port) throws IOException {

        //服务端绑定端口号
        final ServerSocket socket = new ServerSocket(port);

        try {
            while (true) {
                //这里会一直阻塞只到有客户端连接进来
                final Socket clientSocket = socket.accept();

                System.out.println("Accepted connection from " + clientSocket);
                //创建一个线程处理连接
                new Thread(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                        //这里是服务端处理逻辑，读取客户端传来的数据并写回到客户端
                        String line = reader.readLine();
                        System.out.println("Server received:" + line);
                        writer.println(line);
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            clientSocket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 主执行方法
     */
    public static void main(String[] args) throws IOException {
        new PlainEchoServer().serve(9999);
    }
}