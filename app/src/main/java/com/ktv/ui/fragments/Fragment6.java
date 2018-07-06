package com.ktv.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ktv.R;
import com.ktv.bean.MusicPlayBean;
import com.ktv.tools.AlertDialogHelper;
import com.ktv.tools.BtmDialog;
import com.ktv.tools.Logger;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.BaseFr;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * 设置
 */
public class Fragment6 extends BaseFr {

    private static final String TAG="Fragment6";
    private View view;
    private Context mContext;

    private TextView mSetup1;
    private TextView mSetup2;
    private TextView mSetup3;

    public DbManager mDb;

    private SVProgressHUD mSvProgressHUD;

    public static final int Request_Defeated=100;
    public static final int CONSUMING_TIME=200;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Request_Defeated:
                    mSvProgressHUD.dismiss();
                    handler.sendEmptyMessageDelayed(CONSUMING_TIME, 1000);
                    break;

                case CONSUMING_TIME:
                    mSvProgressHUD.showSuccessWithStatus("清理完成");
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment6_layout, container, false);

        initView();
        initLiter();
        return view;
    }

    private void initView(){
        mContext = getActivity();

        mSvProgressHUD = new SVProgressHUD(mContext);

        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

        mSetup1=view.findViewById(R.id.setup1_tvw);
        mSetup2=view.findViewById(R.id.setup2_tvw);
        mSetup3=view.findViewById(R.id.setup3_tvw);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSetup1.requestFocus();
    }


    private void initLiter(){
        mSetup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShortToast(mContext,"连接手机");
            }
        });

        mSetup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShortToast(mContext,"MV清晰度选择");
            }
        });

        mSetup3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaog();
            }
        });
    }

    private void showDiaog(){
        final BtmDialog dialog = new BtmDialog(mContext,"是否清空缓存歌曲?");
        AlertDialogHelper.BtmDialogDerive1(dialog, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSvProgressHUD.showWithStatus("请稍后,清理中...");
                    mDb.delete(MusicPlayBean.class);
                    handler.sendEmptyMessageDelayed(Request_Defeated, 3000);
                } catch (Exception e){
                    Logger.i(TAG,"清理缓存异常"+e.getMessage());
                }
                dialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }
}
