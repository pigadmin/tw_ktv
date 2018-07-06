package com.ktv.event;

public class ErrorMessage {


    public ErrorMessage(String tag, int code) {
        this.tag = tag;
        this.code = code;
    }
    private int code;

    public int getCode() {
        return code;
    }

    private String tag;

    public String gettag() {
        return tag;
    }
}
