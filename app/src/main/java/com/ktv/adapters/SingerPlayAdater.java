package com.ktv.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.bean.SingerNumBean;

import java.util.List;

/**
 * 歌手Adater
 */
public class SingerPlayAdater extends BAdapter<SingerNumBean.SingerBean> {

    Context mContext;

    public SingerPlayAdater(Context context, int layoutId, List<SingerNumBean.SingerBean> list) {
        super(context, layoutId, list);
        this.mContext = context;
    }

    @Override
    public void onInitView(View convertView, int position) {
        TextView singertitle = get(convertView, R.id.singertitle);//歌手名称
        TextView singerTypeName = get(convertView, R.id.singerTypeName);//大陆男演员

        SingerNumBean.SingerBean playBean= getItem(position);
        singertitle.setText(playBean.name);
        singerTypeName.setText(playBean.singerTypeName);
    }
}
