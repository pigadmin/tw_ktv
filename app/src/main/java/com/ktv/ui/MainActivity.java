package com.ktv.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ktv.R;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.AdEntities;
import com.ktv.bean.AdList;
import com.ktv.bean.MusicNumBean;
import com.ktv.bean.MusicPlayBean;
import com.ktv.bean.Update;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.service.MyService;
import com.ktv.tools.ApkUpdate;
import com.ktv.tools.Fragments;
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.Logger;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.fragments.Fragment1;
import com.ktv.ui.fragments.Fragment2;
import com.ktv.ui.fragments.Fragment3;
import com.ktv.ui.fragments.Fragment4;
import com.ktv.ui.fragments.Fragment5;
import com.ktv.ui.fragments.Fragment6;
import com.ktv.ui.play.PlayerActivity;
import com.ktv.views.MyDialogFragment;
import com.squareup.picasso.Picasso;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";

    private RadioButton radioMenu1;
    private RadioButton radioMenu2;
    private RadioButton radioMenu3;
    private RadioButton radioMenu4;
    private RadioButton radioMenu5;
    private RadioButton radioMenu6;

    private Context mContext;

    private WeakHashMap<String, String> weakHashMap = new WeakHashMap<>();

    private List<MusicPlayBean> musicPlayBeans = new ArrayList<>();


    public static final int Search_Music_Success = 100;//搜索歌曲成功
    public static final int Search_Music_Failure = 200;//搜索歌曲失败

    private EditText mSeach;//搜索
    private Button mSure;//确定
    private LinearLayout mllt;//线性布局
    private TextView mInfo1;//歌曲编号
    private TextView mInfo2;//歌手名称
    private TextView mInfo3;//歌曲名称
    public DbManager mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

        initView();
        initLiter();
        update();


        startService(new Intent(this, MyService.class));
        regad();
    }

    private void regad() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.InitAdList);
        intentFilter.addAction(App.UpdateAdList);
        intentFilter.addAction(App.DeleteAdList);
        registerReceiver(receiver, intentFilter);

        ad_alpha = AnimationUtils.loadAnimation(this, R.anim.ad_alpha);
        ad_rotate = AnimationUtils.loadAnimation(this, R.anim.ad_rotate);
        ad_scale = AnimationUtils.loadAnimation(this, R.anim.ad_scale);
        ad_translate = AnimationUtils.loadAnimation(this, R.anim.ad_translate);
    }


    private Animation ad_alpha;
    private Animation ad_rotate;
    private Animation ad_scale;
    private Animation ad_translate;
    private AdList adLists;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(App.InitAdList) || intent.getAction().equals(App.UpdateAdList)) {
                adLists = (AdList) intent.getSerializableExtra("key");
                adEntities = adLists.getAdEntities();
                currentad = 0;

                adhandler.removeMessages(0);
                adhandler.sendEmptyMessage(0);

                adhandler.removeMessages(1);
                adhandler.sendEmptyMessageDelayed(1, adLists.getEndtime() - System.currentTimeMillis());
            } else if (intent.getAction().equals(App.DeleteAdList)) {
                adhandler.sendEmptyMessage(1);
            }
        }
    };
    private List<AdEntities> adEntities;
    private int currentad = 0;
    private Handler adhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    hidead();
                    if (currentad < adEntities.size() - 1) {
                        switch (adEntities.get(currentad).getAppearWay()) {
                            case 1:
                                startAnim(ad_alpha);
                                break;
                            case 2:
                                startAnim(ad_translate);
                                break;
                            case 3:
                                startAnim(ad_scale);
                                break;
                            case 4:
                                startAnim(ad_rotate);
                                break;
                        }
                        adhandler.sendEmptyMessageDelayed(0, adEntities.get(currentad).getPlaytime() * 1000);
                        currentad++;
                    } else {
                        currentad = 0;
                        adhandler.sendEmptyMessage(0);
                    }
                    break;
                case 1:
                    hidead();
                    break;
            }
        }
    };

    private void startAnim(Animation animation) {
        try {
            System.out.println(currentad);
            String[] weizhi = adLists.getPosition().split(",");
            ImageView ad_image = null;
            if (weizhi[currentad].equals("2")) {
                ad_image = ad_top;
            } else if (weizhi[currentad].equals("3")) {
                ad_image = ad_bottom;
            } else if (weizhi[currentad].equals("4")) {
                ad_image = ad_left;
            } else if (weizhi[currentad].equals("5")) {
                ad_image = ad_right;
            }
            ad_image.startAnimation(animation);
            Picasso.with(MainActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_image);
        } catch (Exception e) {
        }


    }


    private void hidead() {
        ad_left.setImageResource(R.color.transparent);
        ad_right.setImageResource(R.color.transparent);
        ad_top.setImageResource(R.color.transparent);
        ad_bottom.setImageResource(R.color.transparent);
    }

    private String update = "update";

    private void update() {
        String url = App.headurl + "upgrade/get?mac=" + App.mac + "&STBtype=2&version=" + App.version;
        Req.get(update, url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    private ImageView ad_left, ad_right, ad_top, ad_bottom;

    private void initView() {
        mContext = MainActivity.this;

        radioMenu1 = findViewById(R.id.rdb1_top_menu);
        radioMenu2 = findViewById(R.id.rdb2_top_menu);
        radioMenu3 = findViewById(R.id.rdb3_top_menu);
        radioMenu4 = findViewById(R.id.rdb4_top_menu);
        radioMenu5 = findViewById(R.id.rdb5_top_menu);
        radioMenu6 = findViewById(R.id.rdb6_top_menu);


        ad_left = findViewById(R.id.ad_left);
        ad_right = findViewById(R.id.ad_right);
        ad_top = findViewById(R.id.ad_top);
        ad_bottom = findViewById(R.id.ad_bottom);

        Fragments.replace(getFragmentManager(), new Fragment2());

    }

    @Override
    protected void onResume() {
        super.onResume();
        radioMenu2.requestFocus();
    }

    private void initLiter() {
//        radioMenu1.setOnFocusChangeListener(this);
//        radioMenu2.setOnFocusChangeListener(this);
//        radioMenu3.setOnFocusChangeListener(this);
//        radioMenu4.setOnFocusChangeListener(this);
//        radioMenu5.setOnFocusChangeListener(this);
//        radioMenu6.setOnFocusChangeListener(this);

        radioMenu1.setOnClickListener(this);
        radioMenu2.setOnClickListener(this);
        radioMenu3.setOnClickListener(this);
        radioMenu4.setOnClickListener(this);
        radioMenu5.setOnClickListener(this);
        radioMenu6.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            exit();
        }
    }

    private long exitTime = 0;
    private String key_temp = "";

    private void exit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            exitTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (key_temp.equals("333")) {
                        showPopuDias();
                    }
                    key_temp = "";
                    break;

                case Search_Music_Success:
                    setSuccessResult();
                    break;
                case Search_Music_Failure:
                    setFailResult();
                    break;
            }
        }
    };
    private MusicPlayBean bianhao;

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            handler.removeMessages(0);
            key_temp += keyCode - 7;
            handler.sendEmptyMessageDelayed(0, 1 * 1000);
        }
        Log.e("fcous", getWindow().getCurrentFocus() + "");
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示 根据编号搜索
     */
    private void showPopuDias() {
        View strView = getLayoutInflater().inflate(R.layout.show_search_number, null, false);
        final PopupWindow window = new PopupWindow(strView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setAnimationStyle(R.style.AnimationFade);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, -140);

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
                    ToastUtils.showShortToast(mContext, "請先搜索歌曲");
                    return;
                }
                try {
                    mDb.save(bianhao);
                    ToastUtils.showShortToast(mContext, "歌曲已添加至已點歌曲");
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showShortToast(mContext, "添加失敗，請勿重複添加");
                }
//                Intent intent = new Intent(mContext, PlayerActivity.class);
//                mContext.startActivity(intent);
            }
        });
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
        Req.get(TAG, url);
    }

    public void onEvent(DataMessage event) {
        Logger.d(TAG, "data.." + event.getData());
        if (event.gettag().equals(TAG)) {
            if (!TextUtils.isEmpty(event.getData())) {
                musicPlayBeans.clear();
                AJson aJsons = GsonJsonUtils.parseJson2Obj(event.getData(), AJson.class);
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
        } else if (event.gettag().equals(update)) {
            AJson<Update> data = App.gson.fromJson(
                    event.getData(), new TypeToken<AJson<Update>>() {
                    }.getType());
            ApkUpdate.install(data.getData().getPath());
        }
    }

    private void isMusicStateList(List<MusicPlayBean> playBeans) {
        if (playBeans != null && !playBeans.isEmpty()) {
            Logger.d(TAG, "list长度1..." + playBeans.size());
            musicPlayBeans.addAll(playBeans);
            handler.sendEmptyMessage(Search_Music_Success);
        } else {
            handler.sendEmptyMessage(Search_Music_Failure);
        }
    }

//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        System.out.println(v);
//        System.out.println(hasFocus);
//        if (hasFocus && !((RadioButton) v).isChecked()) {
//            ((RadioButton) v).setChecked(true);
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            switch (v.getId()) {
//                case R.id.rdb1_top_menu:
//                    transaction.replace(R.id.main, new Fragment1());
//                    break;
//                case R.id.rdb2_top_menu:
//                    transaction.replace(R.id.main, new Fragment2());
//                    break;
//
//                case R.id.rdb3_top_menu:
//                    transaction.replace(R.id.main, new Fragment3());
//                    break;
//
//                case R.id.rdb4_top_menu:
//                    transaction.replace(R.id.main, new Fragment4());
//                    break;
//                case R.id.rdb5_top_menu:
//                    transaction.replace(R.id.main, new Fragment5());
//                    break;
//                case R.id.rdb6_top_menu:
//                    transaction.replace(R.id.main, new Fragment6());
//                    break;
//            }
////            transaction.addToBackStack(null);
//            transaction.commit();
//        }
//
//    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);//动画效果(打开)
        for (int i = 0; i < getFragmentManager().getBackStackEntryCount() - 1; i++) {
            getFragmentManager().popBackStack();
        }
        switch (v.getId()) {
            case R.id.rdb1_top_menu:
                transaction.replace(R.id.main, new Fragment1());
                break;
            case R.id.rdb2_top_menu:
                transaction.replace(R.id.main, new Fragment2());
                break;

            case R.id.rdb3_top_menu:
                transaction.replace(R.id.main, new Fragment3());
                break;

            case R.id.rdb4_top_menu:
                transaction.replace(R.id.main, new Fragment4());
                break;
            case R.id.rdb5_top_menu:
                transaction.replace(R.id.main, new Fragment5());
                break;
            case R.id.rdb6_top_menu:
                transaction.replace(R.id.main, new Fragment6());
                break;
        }
//            transaction.addToBackStack(null);
        transaction.commit();


    }


}
