package com.ktv.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.app.App;
import com.ktv.bean.MusicPlayBean;
import com.ktv.tools.Logger;
import com.ktv.tools.SyncServerdate;
import com.ktv.tools.ToastUtils;

import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 已点歌曲Adater
 */
public class Fragment3Adater_mini extends BAdapter<MusicPlayBean> {

    private static final String TAG = "Fragment3Adater";

    Context mContext;
    public DbManager mDb;
    public ListView listView;
    public TextView mSerachText;
    public TextView mNofoundText;

    public Fragment3Adater_mini(ListView listView, Context context, int layoutId, List<MusicPlayBean> list, DbManager mDb, TextView mSerachText, TextView mNofoundText) {
        super(context, layoutId, list);
        this.mContext = context;
        this.mDb = mDb;
        this.listView = listView;
        this.mSerachText = mSerachText;
        this.mNofoundText = mNofoundText;
    }

    @Override
    public void onInitView(View convertView, final int position) {
        try {
            TextView singertitle = get(convertView, R.id.singername);//歌手名称
            TextView singername = get(convertView, R.id.songname);//歌曲名称
            TextView playType = get(convertView, R.id.playType);// 标识HD or 演唱会
            TextView pointText = get(convertView, R.id.pointText);//未点
            final TextView play = get(convertView, R.id.play);//置顶
            play.setNextFocusUpId(R.id.rdb3_top_menu_dialog);
            final TextView addPlay = get(convertView, R.id.addPlay);//删除
            addPlay.setNextFocusUpId(R.id.rdb3_top_menu_dialog);

            if (position == 0) {
                play.setVisibility(View.INVISIBLE);
                addPlay.setVisibility(View.INVISIBLE);
                pointText.setText("播放中");
            } else {
                play.setVisibility(View.VISIBLE);
                addPlay.setVisibility(View.VISIBLE);
                pointText.setText(R.string.yd);
            }

            final MusicPlayBean playBean = getItem(position);

            StringBuilder sb = new StringBuilder();

            if (playBean.singerName.length() == 2) {
                sb.append(playBean.singerName).insert(1, "\t");
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

            //置顶
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    try {
                        getAllData().remove(playBean);//本地删除对象
                        getAllData().add(1, playBean);//把本地对象添加到第一位

                        mDb.delete(MusicPlayBean.class);
                        mDb.save(getAllData());
                        notifyDataSetChanged();
                        listView.requestFocusFromTouch();
                        listView.setSelection(0);
                        mContext.sendBroadcast(new Intent(App.UpdateMusic));
                    } catch (Exception e) {
                        Logger.i(TAG, "置顶异常e.." + e.getMessage());
                    }
                }
            });

            //删除
            addPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    try {
                        ToastUtils.showShortToast(mContext, "删除成功");
                        mDb.delete(playBean);//先删除DB数据
                        getAllData().remove(playBean);//再删本地列表
                        listView.setSelection(position);
                        notifyDataSetChanged();
                        listView.requestFocusFromTouch();

                        if (getAllData() != null && !getAllData().isEmpty()) {
                            mSerachText.setText("當前已點歌曲 " + getAllData().size() + " 首");
                            mNofoundText.setVisibility(View.GONE);
                        } else {
                            mSerachText.setText("當前暫無已點歌曲");
                            mNofoundText.setText("还未添加歌曲,请先点歌!");
                            mNofoundText.setVisibility(View.VISIBLE);
                        }
                        mContext.sendBroadcast(new Intent(App.UpdateMusic));
                    } catch (Exception e) {
                        Logger.i(TAG, "删除异常e.." + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
