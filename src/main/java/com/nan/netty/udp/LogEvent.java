package com.nan.netty.udp;

import java.net.InetSocketAddress;

public final class LogEvent {

    public static final byte SEPARATOR = (byte) ':';
    //发送方地址
    private final InetSocketAddress source;
    //文件名称
    private final String logfile;
    //实际消息内容
    private final String msg;
    //发送数据的时间戳
    private final long received;

    public LogEvent(String logfile, String msg) {
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received,
                    String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public long getReceivedTimestamp() {
        return received;
    }
}