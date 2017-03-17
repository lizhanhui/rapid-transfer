package com.yeahmobi.lab.protocol;


import io.netty.buffer.ByteBuf;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class DataFrame {

    public static final AtomicLong LONG_ADDER = new AtomicLong();

    private long id;
    private Map<String, String> map;

    private ByteBuf body;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public ByteBuf getBody() {
        return body;
    }

    public void setBody(ByteBuf body) {
        this.body = body;
    }
}
