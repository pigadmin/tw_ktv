package com.ktv.event;

import android.graphics.Bitmap;

public class BitmapMessage {
    public BitmapMessage(String tag, Bitmap bitmap) {
        this.tag = tag;
        this.bitmap = bitmap;
    }

    public String gettag() {
        return tag;
    }

    String tag;

    public Bitmap getBitmap() {
        return bitmap;
    }

    private Bitmap bitmap;


}
