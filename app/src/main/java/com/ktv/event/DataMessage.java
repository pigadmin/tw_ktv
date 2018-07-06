package com.ktv.event;

public class DataMessage {
    public DataMessage(String tag, String data) {
        this.tag = tag;
        this.data = data;
    }

    private String data;

    public String getData() {
        return data;
    }

    private String tag;

    public String gettag() {
        return tag;
    }


}
