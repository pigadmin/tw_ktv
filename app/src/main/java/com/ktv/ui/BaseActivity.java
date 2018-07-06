package com.ktv.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.google.gson.reflect.TypeToken;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.MusicNumBean;
import com.ktv.bean.Update;
import com.ktv.event.DataMessage;
import com.ktv.event.ErrorMessage;
import com.ktv.tools.ApkUpdate;
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.Logger;
import com.ktv.ui.diy.ServerIpDialog;

import de.greenrobot.event.EventBus;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    public void onEvent(ErrorMessage event) {
        System.out.println("00000000000000000000000");
    }

    private App app;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 0:
                    if (key_temp.equals("111")) {
                        new ServerIpDialog(handler, BaseActivity.this).crt();
                    } else if (key_temp.equals("222")) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                    key_temp = "";
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            handler.removeMessages(0);
            key_temp += keyCode - 7;
            handler.sendEmptyMessageDelayed(0, 1 * 1000);
        }
        return super.onKeyDown(keyCode, event);
    }

    private String key_temp = "";
}
