package com.nan.netty.callback;

/**
 * 读数据回调
 */
public interface ReadCallback {

    /**
     * 读完数据回调方法
     */
    void onData(Object data);

    /**
     * 出现异常回调
     */
    void onError(Throwable cause);

}