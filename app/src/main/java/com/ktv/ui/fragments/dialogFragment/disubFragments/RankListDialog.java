package com.ktv.ui.fragments.dialogFragment.disubFragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ktv.R;
import com.ktv.adapters.RankListDialogAdapters;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.GridItem;
import com.ktv.bean.ListItem;
import com.ktv.bean.MusicPlayBean;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.BaseFr;
import com.ktv.views.MyDialogFragment;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜列表
 */
public class RankListDialog extends BaseFr implements RecyclerAdapter.OnItemClickListener, RecyclerAdapter.OnItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "RankListDialog";

    private View view;
    private Activity activity;
    private App app;
    public DbManager mDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rank_list_dialog, container, false);
        activity = getActivity();
        app = (App) activity.getApplication();

        MyDialogFragment.cleanFocus(false);


        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

        find();
        return view;
    }

    //    private RankListDialogAdapter playAdater;
    RankListDialogAdapters playAdater;

    private void init() {
        number.setText("/" + list.size() + "首");

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
            rank_add.setVisibility(View.GONE);
            rank_add.setOnClickListener(this);

//            grids.addItemDecoration(new SpacesItemDecoration(0, 0, 0, 5));
//            layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//            grids.setLayoutManager(layoutManager);

//            playAdater = new RankListDialogAdapter(activity, R.layout.music_play_item_dialog, list, mDb);
            playAdater = new RankListDialogAdapters(activity, R.layout.music_play_item_dialog, list, mDb);
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
    private List<ListItem> list = new ArrayList<>();


    public void onEvent(DataMessage event) {
        if (event.gettag().equals(tag)) {
            AJson<List<ListItem>> data = App.gson.fromJson(
                    event.getData(), new TypeToken<AJson<List<ListItem>>>() {
                    }.getType());
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
                for (ListItem playBean : list) {
                    MusicPlayBean musicPlayBean = new MusicPlayBean();
                    musicPlayBean.id = playBean.getId() + "";
                    musicPlayBean.songnumber = playBean.getSongnumber();
                    musicPlayBean.singerid = playBean.getSingerid() + "";
                    musicPlayBean.name = playBean.getName();
                    musicPlayBean.path = playBean.getPath();
                    musicPlayBean.lanId = playBean.getLanId() + "";
                    musicPlayBean.label = playBean.getLabel();
                    musicPlayBean.singerName = playBean.getSingerName();
                    musicPlayBean.lanName = playBean.getLanName();

                    try {
                        mDb.save(musicPlayBean);
                    } catch (Exception e) {
                        ToastUtils.showShortToast(activity, "部分重複歌曲未被添加");
                    }

                }
                ToastUtils.showShortToast(activity, "歌曲添加成功");
//                Intent intent = new Intent(activity, PlayerActivity.class);
//                activity.startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(page * limit + "----------------------" + (position + 1));
        if (page * limit == (position + 1)) {
            page++;
            ReList();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}