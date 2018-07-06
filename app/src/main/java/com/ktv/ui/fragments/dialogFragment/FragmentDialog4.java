package com.ktv.ui.fragments.dialogFragment;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.ktv.ui.fragments.dialogFragment.disubFragments.RankListDialog;
import com.ktv.ui.fragments.subFragments.SingerTypeFragment;
import com.ktv.ui.rank.RankList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜
 */
public class FragmentDialog4 extends BaseFr implements RecyclerAdapter.OnItemClickListener, RecyclerAdapter.OnItemSelectedListener {

    private static final String TAG="FragmentDialog4";
    private View view;
    private Activity activity;

    private FragmentManager manager;
    private FragmentTransaction ft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.grids_dialog, container, false);
        activity = getActivity();
        find();
        return view;
    }

    private RankGridAdapter adapter;

    private void init() {
        adapter.notifyDataSetChanged();
    }

    private RecyclerView grids;
    private StaggeredGridLayoutManager layoutManager;

    private void find() {
        grids = view.findViewById(R.id.grids);
        grids.addItemDecoration(new SpacesItemDecoration(0, 30, 0, 40));
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        grids.setLayoutManager(layoutManager);

        adapter = new RankGridAdapter(activity, R.layout.adapter_grid_dialog, list);
        grids.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemSelectedListener(this);

        String url = App.headurl + "song/rangking?mac=" + App.mac + "&STBtype=2";
        Req.get(tag, url);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getFragmentManager();
    }

    private String tag = "rangking";
    private List<GridItem> list = new ArrayList<>();

    public void onEvent(DataMessage event) {
        if (event.gettag().equals(tag)) {
            AJson<List<GridItem>> data = App.gson.fromJson(
                    event.getData(), new TypeToken<AJson<List<GridItem>>>() {
                    }.getType());
            if (!list.isEmpty()){
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
//            System.out.println("点击-------------------------" + position);
//            //  创建一个 bundle 传递 数据
//            Bundle bundle = new Bundle();
//            //使用bundle合适的put方法传递数据
//            bundle.putSerializable("key", (Serializable) list.get(position));
//            // 新建一个 fragment
//            RankList fragment = new RankList();
//            // 将数据 保存到 fragment 里面
//            fragment.setArguments(bundle);
//            Fragments.add(getFragmentManager(), fragment);



            toClass(list.get(position));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFocusChange(View view, int position) {
        System.out.println("选中-------------------------" + position);
    }

    /**
     * 切换到 排行榜列表
     * @param item
     */
    private void toClass(GridItem item){
        Bundle bundle = new Bundle();
        ft = manager.beginTransaction();
        RankListDialog mufrt = new RankListDialog();
        ft.replace(R.id.main_popudows, mufrt);
        bundle.putSerializable("key",item);
        mufrt.setArguments(bundle);
        ft.addToBackStack(null);
        ft.commit();
    }
}