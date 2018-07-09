package com.ktv.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.ViewHolders;
import com.ktv.bean.GridItem;
import com.ktv.tools.PicassoUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RankGridAdapter extends RecyclerAdapter<GridItem> {

    public RankGridAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolders holder, GridItem item) {
        ImageView icon = holder.itemView.findViewById(R.id.icon);
        TextView name = holder.itemView.findViewById(R.id.name);

        name.setText(item.getName());
//        System.out.println(item.getIcon());
//        Picasso.with(context).load(Uri.parse("http://192.168.2.5/songSinger/欧美男13.jpg")).into(icon);

        PicassoUtil.picassoAdvanced(context, item.getIcon(), R.mipmap.station_src, R.mipmap.error_src, icon);
    }
}
