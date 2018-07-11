package com.ktv.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.Fragment3Adater;
import com.ktv.bean.MusicPlayBean;
import com.ktv.tools.Logger;
import com.ktv.ui.BaseFr;
import com.ktv.ui.MainActivity;
import com.ktv.ui.diy.Tips;
import com.ktv.ui.play.PlayerActivity;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 已点歌曲
 */
public class Fragment3 extends BaseFr {

    private static final String TAG = "Fragment3";

    private View view;
    private Context mContext;

    private TextView mSerachText;//如果列表有值,则显示 "搜到多少歌曲",反之显示"歌手名称"
    private TextView mNofoundText;

    private ListView listView;
    private Fragment3Adater playAdater;
    private List<MusicPlayBean> musicPlayBeans;

    public static final int Search_Music_Success = 100;//查找歌曲歌曲成功
    public static final int Search_Music_Failure = 200;//查找歌曲失败

    public DbManager mDb;

    private TextView mPlayImme;//立即播放

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Search_Music_Success:
                    mNofoundText.setVisibility(View.GONE);
                    playAdater.notifyDataSetChanged();
                    mSerachText.setText("當前已點歌曲 " + musicPlayBeans.size() + " 首");
                    break;
                case Search_Music_Failure:
                    mNofoundText.setText("还未添加歌曲,请先点歌!");
                    mSerachText.setText("當前暫無已點歌曲");
                    playAdater.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment3, container, false);
        mContext = getActivity();

        ((MainActivity)getActivity()).cleanFocus(true);

        initView();
        initLiter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isMusicStateList();
        Logger.i(TAG, "当前焦点..." + getActivity().getCurrentFocus());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

        musicPlayBeans = new ArrayList<>();

        mSerachText = view.findViewById(R.id.no_found_text_tvw);
        mNofoundText = view.findViewById(R.id.no_found_tvw);
        mPlayImme = view.findViewById(R.id.play_imme_tvw);
        mPlayImme.setVisibility(View.VISIBLE);

        listView = view.findViewById(R.id.listview);
        listView.setItemsCanFocus(true);//设置item项的子控件能够获得焦点（默认为false，即默认item项的子空间是不能获得焦点的）

        playAdater = new Fragment3Adater(listView, getActivity(), R.layout.fragment3_item, musicPlayBeans, mDb,mSerachText,mNofoundText);
        listView.setAdapter(playAdater);
    }

    /**
     * item事件
     */
    private void initLiter() {
        mPlayImme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicPlayBeans.isEmpty()) {
                    Tips.show(getActivity(),
                            getString(R.string.tip_title),
                            getString(R.string.playlist_none));
                    return;
                }
//                    ToastUtils.showShortToast(mContext, "立即播放");
                Intent intent = new Intent(mContext, PlayerActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 查询DB
     */
    private void isMusicStateList() {
        try {
            musicPlayBeans.clear();
            List<MusicPlayBean> playBeans = mDb.selector(MusicPlayBean.class).orderBy("localTime", true).findAll();//数据库查询
            if (playBeans != null && !playBeans.isEmpty()) {
                Logger.d(TAG, "list长度..." + playBeans.size());
                musicPlayBeans.addAll(playBeans);
                handler.sendEmptyMessage(Search_Music_Success);
            } else {
                handler.sendEmptyMessage(Search_Music_Failure);
            }
        } catch (Exception e) {
            Logger.i(TAG, "DB查询异常.." + e.getMessage());
        }
    }
}
