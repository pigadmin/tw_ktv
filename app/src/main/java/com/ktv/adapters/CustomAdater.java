package com.ktv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ktv.R;

public class CustomAdater extends BaseAdapter {

    private LayoutInflater mInflater;
    private String[] articles;

    public CustomAdater(Context context, String[] articles) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.articles = articles;
    }

    @Override
    public int getCount() {
        return articles == null ? 0 : articles.length;
    }

    @Override
    public Object getItem(int position) {
        return articles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.grid_item_x, parent, false);

            holder = new ViewHolder();
            holder.tv = view.findViewById(R.id.mytext);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        holder.tv.setText(articles[position]);

        return view;
    }

    private class ViewHolder {
        public TextView tv;
    }

    public void setData(String[] articles){
        this.articles = articles;
    }
}
