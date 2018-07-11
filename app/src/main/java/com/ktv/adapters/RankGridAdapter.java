package com.ktv.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.ViewHolders;
import com.ktv.bean.GridItem;
import com.ktv.tools.PicassoUtil;

import java.util.List;

public class RankGridAdapter extends RecyclerAdapter<GridItem> {

    private static final String TAG = "RankGridAdapter";

    public RankGridAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolders holder, GridItem item) {
        holder.itemView.setNextFocusUpId(R.id.rdb4_top_menu_main);

        ImageView icon = holder.itemView.findViewById(R.id.icon);
        TextView name = holder.itemView.findViewById(R.id.name);


        name.setText(item.getName());

        try {
            if (TextUtils.isEmpty(item.getNgPath())) {
                icon.setImageResource(R.mipmap.station_src);
            } else {
                String srcPath = PicassoUtil.utf8Togb2312(item.getNgPath());
                PicassoUtil.picassoAdvanced(context, srcPath, R.mipmap.station_src, R.mipmap.error_src, icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
