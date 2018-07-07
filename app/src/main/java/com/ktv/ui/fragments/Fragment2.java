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

import com.ktv.R;
import com.ktv.adapters.Fragment2Adapter;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.SpacesItemDecoration;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.SongNumBean;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.Logger;
import com.ktv.ui.BaseFr;
import com.ktv.ui.fragments.subFragments.SingerTypeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 点歌台(歌曲大类列表)(点歌台) (大中华,欧美,日本,韩国)  1级
 */
public class Fragment2 extends BaseFr {
    private static final String TAG = "Fragment2";

    private View view;
    private Context mContext;

    private RecyclerView mRecyclerView;
    private Fragment2Adapter playAdater;
    private List<SongNumBean.SongLargeBean> mItemList=new ArrayList<>();

    private WeakHashMap<String, String> weakHashMap = new WeakHashMap<>();

    public static final int Search_Music_Success = 100;//查找歌曲歌曲成功
    public static final int Search_Music_Failure = 200;//查找歌曲失败

    private StaggeredGridLayoutManager mLayoutManager;

    private TextView mNoText;//无数据提示

    private FragmentManager manager;
    private FragmentTransaction ft;

    private int mLimit = App.limit;//页码量
    private int mPage = 1;//第几页


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
        view = inflater.inflate(R.layout.fragment2, container, false);
        mContext = getActivity();
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

    /**
     * 初始化View
     */
    private void initView() {
        mItemList = new ArrayList<>();

        mNoText = view.findViewById(R.id.no_tvw);

        view.findViewById(R.id.lableTop).setVisibility(View.INVISIBLE);

        mRecyclerView = view.findViewById(R.id.recyview);


        playAdater = new Fragment2Adapter(getActivity(), R.layout.adapter_grid, mItemList);
        mRecyclerView.setAdapter(playAdater);


        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 30, 0, 40));
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    private View v = null;

    @Override
    public void onResume() {
        super.onResume();
        if (v != null) {
            v.requestFocus();
        } else {
            mRecyclerView.requestFocus();
        }
        System.out.println( getActivity().getCurrentFocus());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v = getActivity().getCurrentFocus();
    }

    /**
     * item事件
     */
    private void initLiter() {
        playAdater.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SongNumBean.SongLargeBean largeBean = mItemList.get(position);
                toClass(largeBean.id, largeBean.name);
            }
        });

        playAdater.setOnItemSelectedListener(new RecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onFocusChange(View view, int position) {
                Logger.i(TAG,"position...."+position);
                int index = position+1;
                if (mPage*mLimit==index){
                    mPage++;
                    getMusicServer();
                }
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
                    SongNumBean numBean = GsonJsonUtils.parseJson2Obj(s, SongNumBean.class);
                    isMusicStateList(numBean.list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void isMusicStateList(List<SongNumBean.SongLargeBean> playBeans) {
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
        weakHashMap.put("mac", App.mac);
        weakHashMap.put("STBtype", "2");
        weakHashMap.put("page", mPage+"");//第几页    不填默认1
        weakHashMap.put("limit", mLimit+"");//页码量   不填默认10，最大限度100
        String url = App.getRqstUrl(App.headurl + "song/getSongType", weakHashMap);

        Logger.i(TAG, "url.." + url);
        Req.get(TAG, url);
    }

    /**
     * 切换到 歌星分类界面
     *
     * @param id
     * @param name
     */
    private void toClass(String id, String name) {
        Bundle bundle = new Bundle();
        ft = manager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);//打开
        SingerTypeFragment mufrt = new SingerTypeFragment();
        ft.replace(R.id.main, mufrt);
        bundle.putString("singerId", id);//歌曲大类ID
        bundle.putString("singerName", name);//歌曲大类名称
        mufrt.setArguments(bundle);
        ft.addToBackStack(null);
        ft.commit();
    }
}
