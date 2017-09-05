package com.nan.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlainEchoClient {

    public void client(int port) throws IOException {

        //创建连接到服务端
        final Socket socket = new Socket("localhost", port);
        //想服务端写入数据
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.write("Hello Server " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
        writer.flush();
        //读取服务端返回的数据并展示
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();
        writer.close();
        reader.close();
        socket.close();
        System.out.println("Received from server: " + line);

    }

    /**
     * 主执行方法
     */
    public static void main(String[] args) throws IOException {
        new PlainEchoClient().client(9999);
    }
}