package com.ktv.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.ViewHolders;
import com.ktv.bean.SongNumBean;
import com.ktv.tools.PicassoUtil;

import java.util.List;

public class Fragment2Adapter extends RecyclerAdapter<SongNumBean.SongLargeBean> {

    private static final String TAG = "Fragment2Adapter";

    public Fragment2Adapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolders holder, SongNumBean.SongLargeBean item) {
        holder.itemView.setNextFocusUpId(R.id.rdb2_top_menu_main);
        ImageView icon = holder.itemView.findViewById(R.id.icon);//图片
        TextView name = holder.itemView.findViewById(R.id.name);//名称

        name.setText(item.name);

        if (TextUtils.isEmpty(item.ngPath)) {
            icon.setImageResource(R.mipmap.station_src);
        } else {
            String srcPath = PicassoUtil.utf8Togb2312(item.ngPath);
            PicassoUtil.picassoAdvanced(context, srcPath, R.mipmap.station_src, R.mipmap.error_src_1, icon);
        }
    }
}
