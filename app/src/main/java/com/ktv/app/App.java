package com.ktv.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.ktv.bean.AdList;
import com.ktv.bean.ListItem;
import com.ktv.service.MyService;
import com.ktv.tools.Logger;
import com.ktv.tools.xDBUtils;

import org.xutils.x;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

public class App extends Application {
    public static final String InitAdList = "InitAdList";
    public static final String UpdateAdList = "UpdateAdList";
    public static final String DeleteAdList = "DeleteAdList";
    public static final String UpdateMusic = "UpdateMusic";
    public xDBUtils DBUtils;

    public static Gson gson;
    public static OkHttpClient client;
    public static RequestQueue queue;
    private static SharedPreferences config;

    @Override
    public void onCreate() {
        super.onCreate();

        initDBUtils();

        config = getSharedPreferences("config", Context.MODE_PRIVATE);
        config();
        getip();
        mac();
        client = new OkHttpClient();
        gson = new GsonBuilder().setDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss").create();

        queue = Volley.newRequestQueue(getApplicationContext());

    }

    private boolean fstart;
    //        private static String ip = "192.168.2.9";
    private static String ip = "192.168.2.25";
//    private static String ip = "192.168.2.4";
//    private static String ip = "192.168.2.11";
//    private static String ip = "192.168.2.7";
    public static String version;

    private void config() {
        try {
            fstart = config.getBoolean("fstart", false);
            if (!fstart) {
                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean("fstart", true);
                Log.d("app", "fstart");
                editor.putString("ip", ip);
                Log.d("ip", "---ip---\n" + ip);
                editor.commit();
            }
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String headurl;
    public static String socketurl;

    private void getip() {
        String tmp = config.getString("ip", "");
        if (!tmp.equals("")) {
            headurl = "http://" + tmp + ":8109/ktv/api/";
//            headurl = "http://" + tmp + ":8080/ktv/api/";
            Log.d("host", "---headurl---\n" + headurl);
            socketurl = "http://" + tmp + ":8000/tv";
            Log.d("host", "---headurl---\n" + socketurl);
        }
    }

    public static String ip() {
        String tmp = config.getString("ip", "");
        return tmp;
    }


    public static void setip(String ip) {
        SharedPreferences.Editor editor = config.edit();
        editor.putString("ip", ip);
        Log.d("ip", "---ip---\n" + ip);
        editor.commit();

    }

    public static String mac;

    private void mac() {
        try {
            Process pro = Runtime.getRuntime().exec(
                    "cat /sys/class/net/eth0/address");
            InputStreamReader inReader = new InputStreamReader(
                    pro.getInputStream());
            BufferedReader bReader = new BufferedReader(inReader);
            String line = null;
            while ((line = bReader.readLine()) != null) {
                mac = line.trim();
            }
//            mac = "00:15:18:17:84:24";
            Log.d("mac", "---mac---\n" + mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T jsonToObject(String json, TypeToken<T> typeToken) {
        if (TextUtils.isEmpty(json) || json.equals("null"))
            return null;
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(json));
            return gson.fromJson(reader, typeToken.getType());
        } catch (Exception e) {
            Logger.e(e);
            return null;
        }
    }

    public static <T> List<T> jsonToList(String strJson, Class<T> cls) {
        try {
            List<T> mList = new ArrayList<T>();

            JsonArray ja = new JsonParser().parse(strJson).getAsJsonArray();
            for (final JsonElement elem : ja) {
                mList.add(new Gson().fromJson(elem, cls));
            }
            return mList;
        } catch (Exception ex) {
            Logger.e(ex);
            return null;
        }
    }

    /**
     * 拼接get请求的url请求地址
     */
    public static String getRqstUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (key != null && params.get(key) != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(params.get(key));
            }
        }
        return builder.toString();
    }

    public void initDBUtils() {
        x.Ext.init(this);
        x.Ext.setDebug(false);// 是否输出debug日志, 开启debug会影响性能
        DBUtils = new xDBUtils();
    }

    public List<View> getcFcous() {
        return cFcous;
    }

    public void setcFcous(List<View> cFcous) {
        this.cFcous = cFcous;
    }

    private List<View> cFcous = new ArrayList<>();


    public ArrayList<ListItem> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ArrayList<ListItem> playlist) {
        this.playlist = playlist;
    }

    private ArrayList<ListItem> playlist = new ArrayList<>();

    /**
     * 列表页码量
     *
     * @return
     */
    public static int getLimitMax() {
        return 90;
    }

    private AdList adLists;

    public AdList getAdLists() {
        return adLists;
    }

    public void setAdLists(AdList adLists) {
        this.adLists = adLists;
    }


}
