package com.ktv.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.reflect.TypeToken;
import com.ktv.R;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.ErrorMsg;
import com.ktv.bean.MusicPlayBean;
import com.ktv.bean.WelcomeAd;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.FULL;
import com.ktv.tools.Logger;
import com.squareup.picasso.Picasso;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    String TAG = "WelcomeActivity";

    public DbManager mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

        try {
            mDb.delete(MusicPlayBean.class);
        } catch (Exception e) {
            Logger.i(TAG, "清理缓存异常" + e.getMessage());
        }

        find();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netreceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitmusic();
        unregisterReceiver(netreceiver);
    }

    private void exitmusic() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private BroadcastReceiver netreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager
                            .getActiveNetworkInfo();
                    int network_type = activeNetworkInfo.getType();
                    System.out.println("网络类型&Net Type：" + network_type);
                    if (network_type > -1) {
                        init();
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    };


    private ImageView ad_image;
    private VideoView ad_video;
    private TextView ad_time;
    private TextView ad_tips;
    private MediaPlayer mediaPlayer;

    private void find() {
        ad_image = findViewById(R.id.ad_image);
        ad_video = findViewById(R.id.ad_video);
        FULL.star(ad_video);
        ad_video.setOnPreparedListener(this);
        ad_video.setOnErrorListener(this);
        ad_video.setOnCompletionListener(this);


        ad_time = findViewById(R.id.ad_time);
        ad_tips = findViewById(R.id.ad_tips);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playmusic();
            }
        });
    }

    private void init() {
        String url = App.headurl + "open/ad?mac=" + App.mac + "&STBtype=2";
        Req.get("welcomead", url);
    }

    private void toClass() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }


    private int currentad;
    private WelcomeAd ad;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (playtime > 0) {
//                        if (!ad_tips.isShown()) {
//                            ad_tips.setVisibility(View.VISIBLE);
//                        }
                        ad_time.setText(playtime + "");
                        playtime--;
                        handler.sendEmptyMessageDelayed(0, 1 * 1000);
                    } else {
                        toClass();
                    }
                    break;
                case 1:

                    if (currentad < welcomeAds.size()) {
                        ad = welcomeAds.get(currentad);
                        switch (ad.getAd().getType()) {
                            case 1:
                                if (ad_image.isShown()) {
                                    ad_image.setVisibility(View.GONE);
                                }
                                if (!ad_video.isShown()) {
                                    ad_video.setVisibility(View.VISIBLE);
                                }
                                videourl = ad.getAd().getPath();
//                                if (mediaPlayer.isPlaying()) {
//                                    mediaPlayer.reset();
//                                    mediaPlayer.stop();
//                                }
                                playvideo();
                                break;
                            case 2:
                                if (!ad_image.isShown()) {
                                    ad_image.setVisibility(View.VISIBLE);
                                }
                                if (ad_video.isShown()) {
                                    ad_video.setVisibility(View.GONE);
                                }
                                videourl = ad.getAd().getBackFile();
                                Picasso.with(WelcomeActivity.this).load(ad.getAd().getPath()).into(ad_image);
                                playmusic();
                                break;
                        }
                        handler.sendEmptyMessageDelayed(1, ad.getPlayTime() * 1000);
                        currentad++;
                    }
//                    else {//循环取消注释
//                        currentad = 0;
//                        handler.sendEmptyMessage(1);
//                    }

                    break;
            }
        }
    };
    private int playtime;
    private List<WelcomeAd> welcomeAds = new ArrayList<WelcomeAd>();

    public void onEvent(DataMessage event) {
        if (event.gettag().equals("welcomead")) {
            try {
                AJson<List<WelcomeAd>> data = App.gson.fromJson(
                        event.getData(), new TypeToken<AJson<List<WelcomeAd>>>() {
                        }.getType());
                welcomeAds = data.getData();
                if (!welcomeAds.isEmpty()) {
                    for (WelcomeAd ad : data.getData()) {
                        playtime += ad.getPlayTime();
                    }
                    handler.sendEmptyMessage(0);
                    handler.sendEmptyMessage(1);
                } else {
                    toClass();
                }
            } catch (Exception e) {
                final ErrorMsg error = App.gson.fromJson(
                        event.getData(), ErrorMsg.class);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        warn(error.getMsg(), "mac：" + App.mac);
                    }
                });
            }
        }
        if (event.gettag().equals("fail")) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    warn(getString(R.string.disconnect), getString(R.string.serverdown));
                }
            });
        }


    }

//    private boolean isto;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE) {
//            if (!isto) {
//                isto = !isto;
//                handler.removeMessages(0);
//                handler.removeMessages(1);
//                toClass();
//            }
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (!isto) {
//                isto = !isto;
//                handler.removeMessages(0);
//                handler.removeMessages(1);
//                toClass();
//            }
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playvideo();
    }

    private String videourl;

    private void playvideo() {

        if (!videourl.equals("")) {
            System.out.println(videourl);
            ad_video.setVideoURI(Uri.parse(videourl));
        }
    }

    private void playmusic() {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(WelcomeActivity.this,
                    Uri.parse(videourl));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

    }

    private AlertDialog.Builder builder;

    private void warn(String title, String msg) {
        try {
            builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(msg)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            init();
                        }
                    }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
        }

    }
}
