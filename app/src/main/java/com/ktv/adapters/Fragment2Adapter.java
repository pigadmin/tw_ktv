package com.ktv.adapters;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.ViewHolders;
import com.ktv.bean.SongNumBean;
import com.ktv.tools.PicassoUtil;

import java.util.List;

public class Fragment2Adapter extends RecyclerAdapter<SongNumBean.SongLargeBean>  {

    public Fragment2Adapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolders holder, SongNumBean.SongLargeBean item) {
        ImageView icon = holder.itemView.findViewById(R.id.icon);//图片
        TextView name = holder.itemView.findViewById(R.id.name);//名称

        name.setText(item.name);
        PicassoUtil.picassoAdvanced(context,item.ngPath,R.mipmap.station_src,R.mipmap.error_src,icon);
    }
}
