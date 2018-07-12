package com.ktv.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.bean.ListItem;
import com.ktv.bean.MusicPlayBean;
import com.ktv.tools.CustomAnimatUtils;
import com.ktv.tools.Logger;
import com.ktv.tools.ToastUtils;

import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.List;

public class RankListAdaters extends BaseAdapter {

    private static final String TAG = "RankListAdaters";

    Context mContext;

    private DbManager mDb;
    int layoutId;
    List<MusicPlayBean> list = new ArrayList<>();
    private List<MusicPlayBean> playlist = new ArrayList<>();

    public RankListAdaters(Context context, int layoutId, List<MusicPlayBean> list, DbManager mDb) {
        this.mContext = context;
        this.mDb = mDb;
        this.layoutId = layoutId;
        this.list = list;
        try {
            playlist = mDb.selector(MusicPlayBean.class).orderBy("localTime", true).findAll();
        } catch (Exception e) {
        }
    }


    private void saveData(MusicPlayBean playBean, boolean isInfo) {
        try {
            mDb.save(playBean);
            if (isInfo) {
                ToastUtils.showShortToast(mContext, playBean.singerName + " 的 " + playBean.name + " 歌曲添加成功");
            }
        } catch (Exception e) {
            Logger.i(TAG, "保存数据异常.." + e.getMessage());
            if (isInfo) {
                ToastUtils.showShortToast(mContext, "此歌曲已被添加");
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private View view;

    @Override
    public View getView(int position, View view2, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(layoutId, null);
        TextView singertitle = view.findViewById(R.id.singername);//歌手名称
        TextView singername = view.findViewById(R.id.songname);//歌曲名称
        TextView playType = view.findViewById(R.id.playType);// 标识HD or 演唱会
        final TextView pointText = view.findViewById(R.id.pointText);//未点
        final TextView play = view.findViewById(R.id.play);//播放
        final TextView addPlay = view.findViewById(R.id.addPlay);//添加

        final MusicPlayBean playBean = list.get(position);

        StringBuilder sb = new StringBuilder();

        if (playBean.singerName.length() == 2) {
            sb.append(playBean.singerName).insert(1, "\t\t");
            singertitle.setText(sb.toString());
        } else {
            singertitle.setText(playBean.singerName);
        }

        singername.setText(playBean.name);

        if (TextUtils.isEmpty(playBean.label)) {
            playType.setVisibility(View.GONE);
        } else {
            playType.setVisibility(View.VISIBLE);
            playType.setText(playBean.label);
        }

        String[] str = (playBean.id).split("\\.0");
        if (playlist != null && !playlist.isEmpty()) {
            for (MusicPlayBean music : playlist) {
                if (str[0].equals(music.id)) {
                    pointText.setText(R.string.yd);
                }
            }
        }


        //播放
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAnimatUtils.showStyle1(play, mContext, R.anim.play_top_1, true);
                saveData(playBean, false);

                Intent intent = new Intent(mContext, com.ktv.ui.PlayerActivity.class);
                mContext.startActivity(intent);
                pointText.setText(R.string.yd);
            }
        });

        //添加
        addPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(playBean, true);
                CustomAnimatUtils.showStyle1(addPlay, mContext, R.anim.addplay_top_1, false);
                pointText.setText(R.string.yd);
            }
        });
        return view;
    }
}
