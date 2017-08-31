package com.nan.netty.callback;

public class ReadFile {

    private String data;

    public ReadFile(String data) {
        this.data = data;
    }

    /**
     * 读取数据模拟
     */
    public void read(ReadCallback readCallback) {
        try {
            //读到数据回调
            readCallback.onData(data);
        } catch (Exception e) {
            //出现异常回调
            readCallback.onError(e);
        }
    }

}
