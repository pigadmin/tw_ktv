package com.ktv.ui.fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.Logger;
import com.ktv.ui.BaseFr;
import com.ktv.ui.MainActivity;
import com.ktv.ui.fragments.subFragments.RankList;

import java.util.ArrayList;
import java.util.List;


/**
 * 排行榜
 */
public class Fragment4 extends BaseFr {

    private static final String TAG = "Fragment4";

    private View view;
    private Context mContext;

    private RecyclerView mRecyclerView;
    private RankGridAdapter playAdater;
    private List<GridItem> mItemList;

    public static final int Search_Music_Success = 100;//查找歌曲歌曲成功
    public static final int Search_Music_Failure = 200;//查找歌曲失败

    private StaggeredGridLayoutManager mLayoutManager;

    private TextView mNoText;//无数据提示

    private FragmentManager manager;
    private FragmentTransaction ft;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Search_Music_Success:
                    mNoText.setVisibility(View.GONE);
                    playAdater.notifyDataSetChanged();
                    break;
                case Search_Music_Failure:
                    mNoText.setVisibility(View.VISIBLE);
                    mNoText.setText("当前无数据");
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.grids, container, false);
        mContext = getActivity();

        ((MainActivity)getActivity()).cleanFocus(true);

        getMusicServer();
        initView();
        initLiter();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getFragmentManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecyclerView!=null){
            mRecyclerView.requestFocus();
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        mItemList = new ArrayList<>();

        mNoText = view.findViewById(R.id.no_tvw);
        mRecyclerView = view.findViewById(R.id.grids);

        playAdater = new RankGridAdapter(getActivity(), R.layout.adapter_grid, mItemList);
        mRecyclerView.setAdapter(playAdater);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 30, 0, 40));
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);



//        mRecyclerView.setNextFocusUpId(R.id.rdb4_top_menu_main);
    }

    /**
     * item事件
     */
    private void initLiter() {
        playAdater.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GridItem largeBean = mItemList.get(position);
                toClass(largeBean);
            }
        });
    }

    public void onEvent(DataMessage event) {
        Logger.d(TAG, "data.." + event.getData());
        if (event.gettag().equals(TAG)) {
            if (!TextUtils.isEmpty(event.getData())) {
                try {
                    AJson aJsons = GsonJsonUtils.parseJson2Obj(event.getData(), AJson.class);
                    String s = GsonJsonUtils.parseObj2Json(aJsons.getData());
                    List<GridItem> playBeans=GsonJsonUtils.parseJson2Obj(s, new TypeToken<List<GridItem>>(){});
                    isMusicStateList(playBeans);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void isMusicStateList(List<GridItem> playBeans) {
        mItemList.clear();
        if (playBeans != null && !playBeans.isEmpty()) {
            Logger.d(TAG, "list长度1..." + playBeans.size());
            mItemList.addAll(playBeans);
        }

        if (mItemList != null && !mItemList.isEmpty()) {
            handler.sendEmptyMessage(Search_Music_Success);
        } else {
            handler.sendEmptyMessage(Search_Music_Failure);
        }
    }

    /**
     * 获取歌曲大类
     */
    private void getMusicServer() {
        String url = App.headurl + "song/rangking?mac=" + App.mac + "&STBtype=2";

        Logger.i(TAG, "url.." + url);
        Req.get(TAG, url);
    }

    private void toClass(GridItem largeBean) {
        Bundle bundle = new Bundle();
        ft = manager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);//打开
        RankList mufrt = new RankList();
        ft.replace(R.id.main, mufrt);
        bundle.putSerializable("key",largeBean);
        mufrt.setArguments(bundle);
        ft.addToBackStack(null);
        ft.commit();
    }
}