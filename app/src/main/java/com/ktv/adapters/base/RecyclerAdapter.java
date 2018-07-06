package com.ktv.adapters.base;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import com.ktv.R;

import java.util.List;

public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolders> implements View.OnClickListener, View.OnFocusChangeListener {

    protected Context context;
    protected LayoutInflater inflate;
    protected List<T> datas;
    private int layout;
    private Animation zoomin,zoomout;
    public RecyclerAdapter(Context context, int layout, List<T> datas) {
        this.context = context;
        this.inflate = LayoutInflater.from(context);
        this.datas = datas;
        this.layout = layout;

        zoomout =AnimationUtils.loadAnimation(context, R.anim.zoomout);
    }

    @Override
    public ViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate.inflate(layout, parent, false);
        ViewHolders holder = new ViewHolders(view);
        view.setOnClickListener(this);
        view.setOnFocusChangeListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolders holder, int position) {
        holder.itemView.setTag(position);
        convert(holder, datas.get(position));
    }

    public abstract void convert(ViewHolders holder, T item);

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private OnItemClickListener mOnItemClickListener = null;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus){
            v.setScaleX(1.17f);
            v.setScaleY(1.17f);
//            ViewCompat.animate(v).scaleX(1.17f).scaleY(1.17f).start();
//            ViewGroup parent = (ViewGroup) v.getParent();
//            parent.requestLayout();
//            parent.invalidate();
        }else{
//            ViewCompat.animate(v).scaleX(1.0f).scaleY(1.0f).start();
//            ViewGroup parent = (ViewGroup) v.getParent();
//            parent.requestLayout();
//            parent.invalidate();
            v.setScaleX(1.0f);
            v.setScaleY(1.0f);
        }


        if (onItemSelectedListener != null) {
            onItemSelectedListener.onFocusChange(v, (int) v.getTag());
        }
    }

    private OnItemSelectedListener onItemSelectedListener = null;

    public interface OnItemSelectedListener {
        void onFocusChange(View view, int position);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }


}
