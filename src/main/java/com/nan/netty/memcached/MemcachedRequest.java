package com.nan.netty.memcached;

import java.util.Random;

public class MemcachedRequest {

    private static final Random rand = new Random();

    //逻辑码，请求对象固定0x80
    private byte magic = (byte) 0x80;

    //操作码，SET或GET等
    private byte opCode;

    //键
    private String key;

    //附加数据标记
    private int flags = 0xdeadbeef;

    //实效，0永不失效
    private int expires;

    //值，SET命令时使用
    private String body;

    //类似唯一编号，方便请求端收到响应时知道是哪次请求
    private int id = rand.nextInt();

    //数据版本校验
    private long cas;

    //是否有附加数据
    private boolean hasExtras;

    public MemcachedRequest(byte opcode, String key, String value) {
        this.opCode = opcode;
        this.key = key;
        this.body = value == null ? "" : value;
        hasExtras = opcode == Opcode.SET;
    }

    public MemcachedRequest(byte opCode, String key) {
        this(opCode, key, null);
    }

    public byte magic() {
        return magic;
    }

    public int opCode() {
        return opCode;
    }

    public String key() {
        return key;
    }

    public int flags() {
        return flags;
    }

    public int expires() {
        return expires;
    }

    public String body() {
        return body;
    }

    public int id() {
        return id;
    }

    public long cas() {
        return cas;
    }

    public boolean hasExtras() {
        return hasExtras;
    }
}