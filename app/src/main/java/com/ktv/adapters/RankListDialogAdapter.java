package com.ktv.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.bean.ListItem;
import com.ktv.bean.MusicPlayBean;
import com.ktv.tools.CustomAnimatUtils;
import com.ktv.tools.Logger;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.play.PlayerActivity;

import org.xutils.DbManager;

import java.util.List;

public class RankListDialogAdapter extends BAdapter<ListItem> {

    private static final String TAG = "MusicPlayAdater";

    Context mContext;

    private DbManager mDb;

    public RankListDialogAdapter(Context context, int layoutId, List<ListItem> list, DbManager mDb) {
        super(context, layoutId, list);
        this.mContext = context;
        this.mDb = mDb;
    }

    @Override
    public void onInitView(View convertView, final int position) {
        TextView singertitle = get(convertView, R.id.singername);//歌手名称
        TextView singername = get(convertView, R.id.songname);//歌曲名称
        TextView playType = get(convertView, R.id.playType);// 标识HD or 演唱会
        TextView pointText = get(convertView, R.id.pointText);//未点
        final TextView play = get(convertView, R.id.play);//播放
        play.setVisibility(View.GONE);
        final TextView addPlay = get(convertView, R.id.addPlay);//添加

        final ListItem playBean = getItem(position);
        final MusicPlayBean musicPlayBean = new MusicPlayBean();
        musicPlayBean.id = playBean.getId() + "";
        musicPlayBean.name = playBean.getName();
        musicPlayBean.path = playBean.getPath();
        musicPlayBean.singerName = playBean.getSingerName();


        singertitle.setText(playBean.getSingerName());
        singername.setText(playBean.getName());

        if (TextUtils.isEmpty(playBean.getLabel())) {
            playType.setVisibility(View.GONE);
        } else {
            playType.setVisibility(View.VISIBLE);
            playType.setText(playBean.getLabel());
        }

        pointText.setText("未點");

        //播放
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAnimatUtils.showStyle1(play,mContext,R.anim.play_top_1,true);

                saveData(musicPlayBean,false);

                Intent intent=new Intent(mContext, PlayerActivity.class);
                mContext.startActivity(intent);
            }
        });

        //添加
        addPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(musicPlayBean,true);
                CustomAnimatUtils.showStyle1(addPlay,mContext,R.anim.addplay_top_1,false);
            }
        });
    }

    private void saveData(MusicPlayBean playBean,boolean isInfo){
        try {
            mDb.save(playBean);
            if (isInfo){
                ToastUtils.showShortToast(mContext,playBean.singerName+" 的 "+playBean.name+" 歌曲添加成功");
            }
        } catch (Exception e){
            Logger.i(TAG,"保存数据异常.."+e.getMessage());
            if (isInfo){
                ToastUtils.showShortToast(mContext,"此歌曲已被添加");
            }
        }
    }
}