package com.nan.netty.callback;

public class Worker {

    public static void main(String[] args) {
        //初始化读取数据对象
        ReadFile readFile = new ReadFile("Hello world");
        //调用读取方法
        readFile.read(new ReadCallback() {
            @Override
            public void onData(Object data) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("on data : " + data);
            }

            @Override
            public void onError(Throwable cause) {
                cause.printStackTrace();
            }
        });
    }

}
