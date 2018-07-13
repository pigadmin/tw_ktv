package com.ktv.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ktv.R;
import com.ktv.app.App;
import com.ktv.event.BitmapMessage;
import com.ktv.event.DataMessage;
import com.ktv.event.ErrorMessage;

import de.greenrobot.event.EventBus;
import okhttp3.Request;
import okhttp3.Response;

public class Req {

    public static void get(final String tag, final String url) {
        Log.d(tag, url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().url(url).build();
                    Response response = App.client.newCall(request).execute();
                    String json = response.body().string();
                    if (response.code() == 200) {
                        Log.d(tag, json);
                        EventBus.getDefault().post(new DataMessage(tag, json));
                    } else {
                        EventBus.getDefault().post(new DataMessage(response.code() + "", json));
                        EventBus.getDefault().post(new ErrorMessage(json, response.code()));
                    }
                } catch (Exception e) {
                    EventBus.getDefault().post(new DataMessage("fail", "fail"));
                }
            }
        }).start();
    }




    public static void img(final String tag, final String url) {
        Log.d(tag, url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().url(url).build();
                    Response response = App.client.newCall(request).execute();
                    if (response.code() == 200) {
                        byte[] b = response.body().bytes();
                        Log.d(tag, b.length + "");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                        EventBus.getDefault().post(new BitmapMessage(tag, bitmap));
                    } else {
                        EventBus.getDefault().post(new ErrorMessage(tag, response.code()));
                    }
                } catch (Exception e) {
                    Log.e(tag, e.toString());
                    EventBus.getDefault().post(new ErrorMessage(tag, 0));
                }
            }
        }).start();
    }

}
