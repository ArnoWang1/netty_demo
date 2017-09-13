package com.nan.netty.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlainOioClient {

    public void client(int port) throws IOException {

        //连接到服务端
        final Socket socket = new Socket("localhost", port);
        //读取服务端返回的数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();
        reader.close();
        socket.close();
        System.out.println("Received from server: " + line);

    }

    /**
     * 主执行方法
     */
    public static void main(String[] args) throws IOException {
        new PlainOioClient().client(9999);
    }
}