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
import com.ktv.ui.PlayerActivity;

import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.List;

public class RankListDialogAdapters extends BaseAdapter {

    private static final String TAG = "RankListDialogAdapter";

    Context mContext;;

    private DbManager mDb;
    List<ListItem> list = new ArrayList<>();
    private List<MusicPlayBean> playlist = new ArrayList<>();
    int layoutId;

    public RankListDialogAdapters(Context context, int layoutId, List<ListItem> list, DbManager mDb) {
        this.mContext = context;
        this.list = list;
        this.layoutId = layoutId;
        this.mDb = mDb;
        try {
            playlist = mDb.selector(MusicPlayBean.class).orderBy("localTime", true).findAll();
        } catch (Exception e) {
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

    @Override
    public View getView(int position, View view2, ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(layoutId, null);
        TextView singertitle = view.findViewById(R.id.singername);//歌手名称
        TextView singername = view.findViewById(R.id.songname);//歌曲名称
        TextView playType = view.findViewById(R.id.playType);// 标识HD or 演唱会
        final TextView pointText = view.findViewById(R.id.pointText);//未点
        final TextView play = view.findViewById(R.id.play);//播放
        play.setVisibility(View.GONE);
        final TextView addPlay = view.findViewById(R.id.addPlay);//添加

        ListItem playBean = list.get(position);

        final MusicPlayBean musicPlayBean = new MusicPlayBean();
        musicPlayBean.id = playBean.getId() + "";
        musicPlayBean.songnumber = playBean.getSongnumber();
        musicPlayBean.singerid = playBean.getSingerid() + "";
        musicPlayBean.name = playBean.getName();
        musicPlayBean.path = playBean.getPath();
        musicPlayBean.lanId = playBean.getLanId() + "";
        musicPlayBean.label = playBean.getLabel();
        musicPlayBean.singerName = playBean.getSingerName();
        musicPlayBean.lanName = playBean.getLanName();

        singertitle.setText(playBean.getSingerName());
        singername.setText(playBean.getName());

        if (TextUtils.isEmpty(playBean.getLabel())) {
            playType.setVisibility(View.GONE);
        } else {
            playType.setVisibility(View.VISIBLE);
            playType.setText(playBean.getLabel());
        }


        String id = musicPlayBean.id;
        for (MusicPlayBean music : playlist) {
            if (id.equals(music.id)) {
                pointText.setText(R.string.yd);
                break;
            }
        }

        //播放
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAnimatUtils.showStyle1(play, mContext, R.anim.play_top_1, true);
                saveData(musicPlayBean, false);

                Intent intent = new Intent(mContext, PlayerActivity.class);
                mContext.startActivity(intent);
                pointText.setText(R.string.yd);
            }
        });

        //添加
        addPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(musicPlayBean, true);
                CustomAnimatUtils.showStyle1(addPlay, mContext, R.anim.addplay_top_1, false);
                pointText.setText(R.string.yd);
            }
        });
        return view;
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
}