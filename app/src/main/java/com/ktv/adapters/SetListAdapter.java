package com.ktv.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.ktv.R;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.ViewHolders;

import java.util.List;

public class SetListAdapter extends RecyclerAdapter<Integer> {

    public SetListAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolders holder, Integer item) {
        ImageButton set_icon = holder.itemView.findViewById(R.id.set_icon);
        set_icon.setBackgroundResource(item);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
