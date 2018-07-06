package com.ktv.adapters;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.ViewHolders;
import com.ktv.bean.GridItem;
import com.ktv.bean.ListItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RankListAdapter_old extends RecyclerAdapter<ListItem> {

    public RankListAdapter_old(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolders holder, ListItem item) {
//        TextView music_name = holder.itemView.findViewById(R.id.music_name);
//        music_name.setText(item.getName());
//        TextView singer_name = holder.itemView.findViewById(R.id.singer_name);
//        singer_name.setText(item.getSingerName());
//        TextView music_status = holder.itemView.findViewById(R.id.music_status);

    }

}
