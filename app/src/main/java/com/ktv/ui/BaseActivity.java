package com.ktv.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ktv.R;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.MusicNumBean;
import com.ktv.bean.MusicPlayBean;
import com.ktv.bean.Update;
import com.ktv.event.DataMessage;
import com.ktv.event.ErrorMessage;
import com.ktv.net.Req;
import com.ktv.tools.ApkUpdate;
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.Logger;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.diy.ServerIpDialog;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import de.greenrobot.event.EventBus;
import okhttp3.Request;
import okhttp3.Response;

public class BaseActivity extends Activity {
    String TAG = "BaseActivity";
    public DbManager mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);
        super.onCreate(savedInstanceState);
    }


    public void onEvent(ErrorMessage event) {
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
                    } else if (key_temp.equals("333")) {
                        showPopuDias();
                    }
                    key_temp = "";
                    break;
                case 1:
                    setSuccessResult();
                    break;
                case 2:
                    setFailResult();
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

    private EditText mSeach;//搜索
    private Button mSure;//确定
    private LinearLayout mllt;//线性布局
    private TextView mInfo1;//歌曲编号
    private TextView mInfo2;//歌手名称
    private TextView mInfo3;//歌曲名称

    private WeakHashMap<String, String> weakHashMap = new WeakHashMap<>();

    private List<MusicPlayBean> musicPlayBeans = new ArrayList<>();
    private MusicPlayBean bianhao;

    /**
     * 显示 根据编号搜索
     */
    private void showPopuDias() {
        try {
            View strView = getLayoutInflater().inflate(R.layout.show_search_number, null, false);
            final PopupWindow window = new PopupWindow(strView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            window.setAnimationStyle(R.style.AnimationFade);
            window.setOutsideTouchable(true);
            window.setBackgroundDrawable(new BitmapDrawable());
            window.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, -140);

            mSeach = strView.findViewById(R.id.seach_edt);//输入框
            mSure = strView.findViewById(R.id.sure_btn);//确定
            mllt = strView.findViewById(R.id.llt);//线性布局
            mInfo1 = strView.findViewById(R.id.info_1_tvw);//歌曲编号
            mInfo2 = strView.findViewById(R.id.info_2_tvw);//歌手名称
            mInfo3 = strView.findViewById(R.id.info_3_tvw);//歌曲名称

            mSeach.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.toString().length() == 6) {
                        getServer(mSeach.getText().toString().trim());
                    } else {
                        if (mllt.isShown()) {
                            musicPlayBeans.clear();
                            mllt.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });
            mSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                mSeach.setText(null);
                    if (musicPlayBeans.isEmpty()) {
                        ToastUtils.showShortToast(BaseActivity.this, "請先搜索歌曲");
                        return;
                    }
                    String[] str = (bianhao.id).split("\\.0");
                    Logger.i(TAG, "str[0].." + str[0]);
                    try {
                        bianhao.id = str[0];//这里是去除id 6.0转为 6;
                        mDb.save(bianhao);
                        ToastUtils.showShortToast(BaseActivity.this, "歌曲已添加至已點歌曲");
                        sendBroadcast(new Intent(App.UpdateMusic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showShortToast(BaseActivity.this, "添加失敗，請勿重複添加");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接口获取:歌曲列表
     */
    private void getServer(String text) {
        weakHashMap.put("mac", App.mac);
        weakHashMap.put("STBtype", "2");
        weakHashMap.put("page", "1");//第几页    不填默认1
        weakHashMap.put("limit", App.limit + "");//页码量   不填默认10，最大限度100
        weakHashMap.put("singerId", null);//歌手id
        weakHashMap.put("songnumber", text);//歌曲编号
        weakHashMap.put("zhuyin", null);//拼音
        weakHashMap.put("pinyin", null);//注音
        weakHashMap.put("vietnam", null);//越南
        weakHashMap.put("japanese", null);//日文

        String url = App.getRqstUrl(App.headurl + "song", weakHashMap);
        get(TAG, url);
    }

    private void get(final String tag, final String url) {
        Log.d(tag, url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().url(url).build();
                    Response response = App.client.newCall(request).execute();
                    String json = response.body().string();
                    if (response.code() == 200) {
                        if (!TextUtils.isEmpty(json)) {
                            musicPlayBeans.clear();
                            AJson aJsons = GsonJsonUtils.parseJson2Obj(json, AJson.class);
                            String s = GsonJsonUtils.parseObj2Json(aJsons.getData());
                            Logger.i(TAG, "s.." + s);
                            MusicNumBean numBean = GsonJsonUtils.parseJson2Obj(s, MusicNumBean.class);
                            if (numBean == null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!mllt.isShown()) {
                                            mllt.setVisibility(View.VISIBLE);
                                        }
                                        mInfo1.setText("未開通編號點歌功能!");
                                        mInfo2.setText(null);
                                        mInfo3.setText(null);
                                    }
                                });
                            } else {
                                isMusicStateList(numBean.list);
                            }

                        }
                    } else {

                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    private void isMusicStateList(List<MusicPlayBean> playBeans) {
        if (playBeans != null && !playBeans.isEmpty()) {
            Logger.d(TAG, "list长度1..." + playBeans.size());
            musicPlayBeans.addAll(playBeans);
            handler.sendEmptyMessage(1);
        } else {
            handler.sendEmptyMessage(2);
        }
    }

    private void setSuccessResult() {
        bianhao = musicPlayBeans.get(0);
        if (bianhao != null) {
            if (!mllt.isShown()) {
                mllt.setVisibility(View.VISIBLE);
            }
            mInfo1.setText(bianhao.songnumber);
            mInfo2.setText(bianhao.singerName);
            mInfo3.setText(bianhao.name);
        }
    }

    private void setFailResult() {
        if (!mllt.isShown()) {
            mllt.setVisibility(View.VISIBLE);
        }
        mInfo1.setText("未搜索到編號");
        mInfo2.setText(mSeach.getText().toString().trim());
        mInfo3.setText("相關歌曲");
    }
}
