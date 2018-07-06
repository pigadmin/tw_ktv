package com.ktv.adapters.base;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.bean.SingerNumBean;
import com.ktv.tools.PicassoUtil;

import java.util.List;

public class SingerListAdapter extends RecyclerAdapter<SingerNumBean.SingerBean>  {

    public SingerListAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolders holder, SingerNumBean.SingerBean item) {
        ImageView icon = holder.itemView.findViewById(R.id.icon);//图片
        TextView name = holder.itemView.findViewById(R.id.name);//名称

        name.setText(item.name);
        PicassoUtil.picassoAdvanced(context,item.ngPath,R.mipmap.station_src,R.mipmap.error_src,icon);
    }
}
