package com.ktv.ui.fragments.subFragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.RankListAdaters;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.GridItem;
import com.ktv.bean.MusicNumBean;
import com.ktv.bean.MusicPlayBean;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.BaseFr;
import com.ktv.ui.MainActivity;
import com.ktv.ui.PlayerActivity;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜列表
 */
public class RankList extends BaseFr implements RecyclerAdapter.OnItemClickListener, RecyclerAdapter.OnItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private View view;
    private Activity activity;
    private App app;
    public DbManager mDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rank_list, container, false);
        activity = getActivity();
        app = (App) activity.getApplication();

        ((MainActivity) getActivity()).cleanFocus(false);

        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

        find();
        return view;
    }

    //    private RankListAdapter playAdater;
    private RankListAdaters playAdater;

    private void init() {

        number.setText("/" + numBean.totalCount.split("\\.0")[0] + "首");

        if (page == 1) {
            playAdater.notifyDataSetChanged();
        }
    }

    private RecyclerView grids;
    private StaggeredGridLayoutManager layoutManager;
    private GridItem item;
    private TextView title;
    private TextView number;
    private ListView lists;
    private Button rank_add;
    private int page = 1;
    private int limit = App.limit;


    private void find() {
        try {
            title = view.findViewById(R.id.title);
            number = view.findViewById(R.id.number);
            grids = view.findViewById(R.id.grids);
            lists = view.findViewById(R.id.lists);
            lists.setItemsCanFocus(true);

            rank_add = view.findViewById(R.id.rank_add);
            rank_add.setOnClickListener(this);

//            grids.addItemDecoration(new SpacesItemDecoration(0, 0, 0, 5));
//            layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//            grids.setLayoutManager(layoutManager);

//            playAdater = new RankListAdapter(activity, R.layout.adapter_list, list, mDb);
            playAdater = new RankListAdaters(activity, R.layout.adapter_list, list, mDb);
            lists.setAdapter(playAdater);
            lists.setOnItemSelectedListener(this);

            item = (GridItem) getArguments().getSerializable("key");
            title.setText(item.getName());
            ReList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View v = null;

    @Override
    public void onResume() {
        super.onResume();
        if (v != null) {
            v.requestFocus();
        } else {
            lists.requestFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v = getActivity().getCurrentFocus();
    }

    private void ReList() {
        String url = App.headurl + "song/getRangeSong?mac=" + App.mac + "&STBtype=2" + "&rangId=" + item.getId() + "&page=" + page + "&limit=" + limit;
        Req.get(tag, url);
    }


    private String tag = "RankList";
    private List<MusicPlayBean> list = new ArrayList<>();
    private MusicNumBean numBean;

    public void onEvent(DataMessage event) {
        if (event.gettag().equals(tag)) {
            if (!TextUtils.isEmpty(event.getData())) {
                AJson aJsons = GsonJsonUtils.parseJson2Obj(event.getData(), AJson.class);
                String s = GsonJsonUtils.parseObj2Json(aJsons.getData());
                numBean = GsonJsonUtils.parseJson2Obj(s, MusicNumBean.class);
                list.addAll(numBean.list);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        init();
                    }
                });
            }
        }


    }

    private Handler handler = new Handler();

    @Override
    public void onItemClick(View view, int position) {
        System.out.println("点击" + position);
//        app.getPlaylist().add(list.get(position));
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("key", (Serializable) list.get(position));
//        .putExtras(bundle)
//        startActivity(new Intent(activity, PlayerActivity.class));
    }

    @Override
    public void onFocusChange(View view, int position) {
        System.out.println("选中" + position);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rank_add:
                for (MusicPlayBean playBean : list) {
                    String[] str = (playBean.id).split("\\.0");
                    try {
                        playBean.id = str[0];
                        mDb.save(playBean);
                    } catch (Exception e) {
                        ToastUtils.showShortToast(activity, "部分重複歌曲未被添加");
                    }
                }
                ToastUtils.showShortToast(activity, "歌曲添加成功，馬上爲您播放。");
                Intent intent = new Intent(activity, PlayerActivity.class);
                activity.startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(page * limit + "----------------------" + (position + 1) );
        if (page * limit == (position + 1)) {
            page++;
            ReList();
        }
        lists.setSelectionFromTop(position, view.getHeight() * 5);
//        lists.setSelectionFromTop(position, 200);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}