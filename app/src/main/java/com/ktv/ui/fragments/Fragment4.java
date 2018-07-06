package com.ktv.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.ktv.R;
import com.ktv.adapters.RankGridAdapter;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.SpacesItemDecoration;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.GridItem;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.Fragments;
import com.ktv.ui.BaseFr;
import com.ktv.ui.rank.RankList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.FOCUS_AFTER_DESCENDANTS;


/**
 * 排行榜
 */
public class Fragment4 extends BaseFr implements RecyclerAdapter.OnItemClickListener, RecyclerAdapter.OnItemSelectedListener {

    private static final String TAG = "Fragment4";
    private View view;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.grids, container, false);
        activity = getActivity();
        find();
        return view;
    }


    private View v = null;

    @Override
    public void onResume() {
        super.onResume();
        if (v != null) {
            v.requestFocus();
        } else {
            grids.requestFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v = getActivity().getCurrentFocus();
    }

    private RankGridAdapter adapter;

    private void init() {
        adapter.notifyDataSetChanged();
    }

    private RecyclerView grids;
    private StaggeredGridLayoutManager layoutManager;

    private void find() {
        grids = view.findViewById(R.id.grids);
//        grids.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        grids.setClipToPadding(false);
        grids.setClipChildren(false);

        grids.addItemDecoration(new SpacesItemDecoration(0, 30, 0, 40));
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        grids.setLayoutManager(layoutManager);

        adapter = new RankGridAdapter(activity, R.layout.adapter_grid, list);
        grids.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemSelectedListener(this);

        String url = App.headurl + "song/rangking?mac=" + App.mac + "&STBtype=2";
        Req.get(tag, url);
    }

    private String tag = "rangking";
    private List<GridItem> list = new ArrayList<>();

    public void onEvent(DataMessage event) {
        if (event.gettag().equals(tag)) {
            AJson<List<GridItem>> data = App.gson.fromJson(
                    event.getData(), new TypeToken<AJson<List<GridItem>>>() {
                    }.getType());
            if (!list.isEmpty()) {
                list.clear();
            }
            list.addAll(data.getData());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            });
        }

    }

    private Handler handler = new Handler();

    @Override
    public void onItemClick(View view, int position) {
        try {
            System.out.println("点击-------------------------" + position);
            //  创建一个 bundle 传递 数据
            Bundle bundle = new Bundle();
            //使用bundle合适的put方法传递数据
            bundle.putSerializable("key", (Serializable) list.get(position));
            // 新建一个 fragment
            RankList fragment = new RankList();
            // 将数据 保存到 fragment 里面
            fragment.setArguments(bundle);
            Fragments.replace(activity.getFragmentManager(), fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFocusChange(View view, int position) {
        System.out.println("选中-------------------------" + position);
    }


}