package com.ktv.ui.play;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.ktv.R;
import com.ktv.app.App;
import com.ktv.bean.AdEntities;
import com.ktv.bean.AdList;
import com.ktv.bean.MusicPlayBean;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.FULL;
import com.ktv.tools.Logger;
import com.ktv.tools.LtoDate;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.BaseActivity;
import com.ktv.ui.MainActivity;
import com.ktv.ui.diy.Tips;
import com.ktv.ui.fragments.subFragments.MusicSubFragment;
import com.ktv.views.MyDialogFragment;
import com.squareup.picasso.Picasso;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private String tag = "PlayerActivity";
    private final int hide = 1;
    private final int updateprogress = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case hide:
                    if (tips.isShown()) {
                        tips.setVisibility(View.GONE);
                    }
                    break;
                case updateprogress:
                    try {
                        current_progress.setText(LtoDate.ms(player.getCurrentPosition()));
                        music_progress.setProgress(player.getCurrentPosition());

                        handler.sendEmptyMessageDelayed(updateprogress, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        app = (App) getApplication();
        find();
        init();


        regad();
        checkad();
    }

    private void checkad() {
        try {
            adLists = app.getAdLists();
            adEntities = adLists.getAdEntities();
            if (adLists == null)
                return;
            if (adEntities.isEmpty())
                return;

            adhandler.sendEmptyMessage(0);
            Log.e(tag, "广告结束时间" + LtoDate.HMmd(adLists.getEndtime() - System.currentTimeMillis()));

            adhandler.sendEmptyMessageDelayed(1, adLists.getEndtime() - System.currentTimeMillis());
        } catch (Exception e) {
        }

    }

    private void regad() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.UpdateMusic);
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
    private AdList adLists = new AdList();


    private List<AdEntities> adEntities = new ArrayList<>();
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
            String weizhi = adLists.getPosition();
            if (weizhi.contains("2")) {
                ad_top.startAnimation(animation);
                Picasso.with(PlayerActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_top);
            }
            if (weizhi.contains("3")) {
                ad_bottom.startAnimation(animation);
                Picasso.with(PlayerActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_bottom);
            }
            if (weizhi.contains("4")) {
                ad_left.startAnimation(animation);
                Picasso.with(PlayerActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_left);
            }
            if (weizhi.contains("5")) {
                ad_right.startAnimation(animation);
                Picasso.with(PlayerActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_right);
            }
        } catch (Exception e) {
        }


    }


    private void hidead() {
        ad_left.setImageResource(R.color.transparent);
        ad_right.setImageResource(R.color.transparent);
        ad_top.setImageResource(R.color.transparent);
        ad_bottom.setImageResource(R.color.transparent);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(App.UpdateMusic)) {
                init();
            } else if (intent.getAction().equals(App.InitAdList) || intent.getAction().equals(App.UpdateAdList)) {
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

    public void onEvent(DataMessage event) {

    }

    private List<MusicPlayBean> musicPlayBeans = new ArrayList<>();

    private void init() {
        try {
            musicPlayBeans = mDb.selector(MusicPlayBean.class).orderBy("localTime", true).findAll();//数据库查询
            if (musicPlayBeans.isEmpty()) {
//                Tips.show(PlayerActivity.this,
//                        getString(R.string.tip_title),
//                        getString(R.string.playlist_none));
                return;
            }
            Logger.d(tag, "list长度" + musicPlayBeans.size());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!player.isPlaying()) {
                        play();
                    } else {
                        updateinfo();
                    }

                }
            });
        } catch (Exception e) {
            Logger.i(tag, "DB查询异常.." + e.getMessage());
        }
    }


    private VideoView player;
    private TextView play_info;
    private RelativeLayout tips;
    private DbManager mDb;
    private SeekBar music_progress;
    private TextView current_progress, total_progress;
    private RadioButton btn_yd, btn_replay, btn_dgt, btn_pause, btn_next, btn_yc, btn_tyt;
    private ImageView ad_left, ad_right, ad_top, ad_bottom;

    private void find() {
        player = findViewById(R.id.player);
        FULL.star(player);
        play_info = findViewById(R.id.play_info);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);


        tips = findViewById(R.id.tips);


        music_progress = findViewById(R.id.music_progress);

        music_progress.setOnSeekBarChangeListener(this);

        current_progress = findViewById(R.id.current_progress);
        total_progress = findViewById(R.id.total_progress);
        btn_yd = findViewById(R.id.btn_yd);
        btn_yd.setOnClickListener(this);
        btn_replay = findViewById(R.id.btn_replay);
        btn_replay.setOnClickListener(this);
        btn_dgt = findViewById(R.id.btn_dgt);
        btn_dgt.setOnClickListener(this);
        btn_pause = findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(this);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_yc = findViewById(R.id.btn_yc);
        btn_yc.setOnClickListener(this);
        btn_tyt = findViewById(R.id.btn_tyt);
        btn_tyt.setOnClickListener(this);

        ad_left = findViewById(R.id.ad_left);
        ad_right = findViewById(R.id.ad_right);
        ad_top = findViewById(R.id.ad_top);
        ad_bottom = findViewById(R.id.ad_bottom);

        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

    }

    private MusicPlayBean current;
    private MusicPlayBean next;

    private void updateinfo() {
        current = musicPlayBeans.get(0);
        if (musicPlayBeans.size() > 1) {
            next = musicPlayBeans.get(1);
        }
        String info = getString(R.string.play_now) + current.name + "\n"
                + getString(R.string.play_next) + (musicPlayBeans.size() > 1 ? next.name : getString(R.string.none)) + "\n"
                + getString(R.string.play_num) + musicPlayBeans.size();

        play_info.setText(info);
        Log.d(tag, info);
    }


    //    String testurl = "http://mx.djkk.com/mix/2018/2018-3/2018-3-13/201831311131.m4a";
//    private String testurl = "http://183.60.197.29/20/x/h/k/k/xhkkasvwnohcmcnivchcmpnugztujq/hc.yinyuetai.com/7C110164442D4B67745E9D3E77F66929.mp4";

    private void play() {//播放、重唱
        try {
            if (!musicPlayBeans.isEmpty()) {
                updateinfo();
                String url = musicPlayBeans.get(0).path;
//                String url = testurl;
                Log.d(tag, url);
                player.setVideoURI(Uri.parse(url));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer mediaPlayer;

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer = mp;
        mp.start();
        total_progress.setText(LtoDate.ms(mediaPlayer.getDuration()));
        music_progress.setMax(mediaPlayer.getDuration());
        handler.sendEmptyMessage(updateprogress);

//        record();
    }

    private void record() {
        String url = App.headurl + "song/record/add?mac=" + App.mac + "&STBtype=2&sid=" + Integer.valueOf(current.id);
        Req.get(tag, url);

    }


    @Override

    public void onCompletion(MediaPlayer mp) {


        next();
    }

    private void next() {//下一首、切歌
        try {
            if (!musicPlayBeans.isEmpty()) {
                mDb.delete(current);
                musicPlayBeans.remove(0);
                if (musicPlayBeans.isEmpty()) {
//                    Tips.show(PlayerActivity.this,
////                            getString(R.string.tip_title),
////                            getString(R.string.playlist_last));
                    player.stopPlayback();
                } else {
                    play();
                }
            } else {
                Tips.show(PlayerActivity.this,
                        getString(R.string.tip_title),
                        getString(R.string.playlist_none));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            show();
        }
        if (tips.isShown()) {
            handler.removeMessages(hide);
            handler.sendEmptyMessageDelayed(hide, 10 * 1000);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.tip_title).setMessage(R.string.tip_exit)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            show();
            if (tips.isShown()) {
                handler.removeMessages(hide);
                handler.sendEmptyMessageDelayed(hide, 10 * 1000);
            }
        }
        return super.onTouchEvent(event);
    }

    //    @Override
//    public void onBackPressed() {
//        //        super.onBackPressed();
//        if (getFragmentManager().getBackStackEntryCount() > 1) {
//            getFragmentManager().popBackStack();
//        } else {
//            exit();
//        }
//    }

    private void show() {
        if (!tips.isShown()) {
            btn_yd.requestFocus();
            tips.setVisibility(View.VISIBLE);
            handler.removeMessages(hide);
            handler.sendEmptyMessageDelayed(hide, 10 * 1000);
        }
    }


    @Override
    protected void onStop() {
        player.stopPlayback();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player = null;
        unregisterReceiver(receiver);

    }


    int ybc = 1;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeMessages(updateprogress);
        mediaPlayer.seekTo(seekBar.getProgress());
        handler.sendEmptyMessage(updateprogress);
    }

    private MediaPlayer.TrackInfo[] trackInfo;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yd:
                showDialogFragment(true);
                break;
            case R.id.btn_replay:
                play();
                break;
            case R.id.btn_dgt:
                showDialogFragment(false);
                break;
            case R.id.btn_pause:
                if (btn_pause.getText().toString().equals(getString(R.string.pause))) {
                    player.pause();
                    btn_pause.setText(getString(R.string.play));
                } else if (btn_pause.getText().toString().equals(getString(R.string.play))) {
                    player.start();
                    btn_pause.setText(getString(R.string.pause));
                }

                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_yc:


                try {
                    trackInfo = mediaPlayer.getTrackInfo();
                    Log.d(tag, trackInfo.length + "---ybc");
                    for (int i = 0; i < trackInfo.length; i++) {
                        Log.d(tag, trackInfo[i].getTrackType() + "---ybc");
                    }
                    //測試
                    if (btn_yc.getText().toString().equals(getString(R.string.yc))) {
                        btn_yc.setText(getString(R.string.bc));
                    } else if (btn_yc.getText().toString().equals(getString(R.string.bc))) {
                        btn_yc.setText(getString(R.string.yc));
                    }
                    //測試  後期下移到下列判斷
                    if (trackInfo.length > 2) {
                        mediaPlayer.selectTrack(ybc == 1 ? 2 : 1);
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case R.id.btn_tyt:
//                showtyt();

                break;


        }
    }

    private PopupWindow popupWindow;

    private void showtyt() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_tyt, null);

        popupWindow = new PopupWindow(contentView, 320, 180, true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);


    }

    private void showDialogFragment(boolean PoDisplay) {
        FragmentManager fm = getFragmentManager();
        MyDialogFragment dialogFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("PoDisplay", PoDisplay);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, tag);


    }


}
