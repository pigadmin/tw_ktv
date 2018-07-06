package com.ktv.tools;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkhttpUtils {

    //请求方式
   public enum RequestMethod {
        GET, POST
    }

    public static void doStart(RequestMethod method, String url, FormBody.Builder builder, Callback callback) {
        Request request = null;
        if (method == RequestMethod.GET) {
            request = new Request.Builder().url(url).build();
        } else if (method == RequestMethod.POST) {
            request = new Request.Builder().url(url).post(builder.build()).build();
        }
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }
}
