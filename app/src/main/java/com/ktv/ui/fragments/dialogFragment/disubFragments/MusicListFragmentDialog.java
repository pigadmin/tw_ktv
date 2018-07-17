package com.ktv.ui.fragments.dialogFragment.disubFragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.MusicListFragmentDialogAdaters;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.MusicNumBean;
import com.ktv.bean.MusicPlayBean;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.Logger;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.BaseFr;
import com.ktv.views.MyDialogFragment;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * (点歌台)通过歌星搜索歌曲的列表  4级
 */
public class MusicListFragmentDialog extends BaseFr {

    private static final String TAG = "MusicListFragmentDialog";

    private View view;
    private Context mContext;

    private TextView mSerachText;//如果列表有值,则显示 "搜到多少歌曲",反之显示"歌手名称"
    private TextView mNofoundText;

    private ListView listView;
    private MusicListFragmentDialogAdaters playAdater;
    private List<MusicPlayBean> musicPlayBeans;

    public static final int Search_Music_Success = 100;//搜索歌曲成功
    public static final int Search_Music_Failure = 200;//搜索歌曲失败

    private WeakHashMap<String, String> weakHashMap = new WeakHashMap<>();

    private String mSingerId;//歌手ID
    private String mSingerName;//歌手名称

    public DbManager mDb;

    private int mLimit = App.limit;//页码量
    private int mPage = 1;//第几页

    public int totalCount = 0;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case Search_Music_Success:
                        mNofoundText.setVisibility(View.GONE);
                        playAdater.notifyDataSetChanged();
                        mSerachText.setText("搜索到 " + mSingerName + " 的歌曲" + totalCount + "首");
                        break;
                    case Search_Music_Failure:
                        mNofoundText.setVisibility(View.VISIBLE);
                        mNofoundText.setText("未能搜索到您想要的歌曲!");
                        mSerachText.setText(mSingerName);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.music_fragment_dialog, container, false);
        getIntentData();
        mContext = getActivity();

        MyDialogFragment.cleanFocus(false);

        initView();
        initLiter();
        return view;
    }

    /**
     * Bundle传值
     */
    private void getIntentData() {
        mSingerId = getArguments().getString("singerid");
        mSingerName = getArguments().getString("singerName");
        Logger.i(TAG, "mSingerId..." + mSingerId + "..mSingerName..." + mSingerName);
        getMusicServer(mSingerId, mPage, mLimit);
    }

    private void getMusicServer(String id, int page, int limit) {
        weakHashMap.put("mac", App.mac);
        weakHashMap.put("STBtype", "2");
        weakHashMap.put("page", page + "");//第几页    不填默认1
        weakHashMap.put("limit", limit + "");//页码量   不填默认10，最大限度100
        weakHashMap.put("singerId", id);//歌手id
        weakHashMap.put("songnumber", null);//歌曲编号
        String url = App.getRqstUrl(App.headurl + "song/getsong", weakHashMap);

        Logger.i(TAG, "点击item请求的url.." + url);
        Req.get(TAG, url);
    }

    private void initView() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

        musicPlayBeans = new ArrayList<>();

        mSerachText = view.findViewById(R.id.no_found_text_tvw);
        mNofoundText = view.findViewById(R.id.no_found_tvw);
        listView = view.findViewById(R.id.listview);
        listView.setItemsCanFocus(true);//设置item项的子控件能够获得焦点（默认为false，即默认item项的子空间是不能获得焦点的）

        playAdater = new MusicListFragmentDialogAdaters(getActivity(), R.layout.music_play_item_dialog, musicPlayBeans, mDb);
        listView.setAdapter(playAdater);
    }

    private void initLiter() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.i(TAG, "position.." + position);
                ToastUtils.showShortToast(mContext, "歌星搜索歌曲的列表position.." + position);
            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = position + 1;
                if (mPage * mLimit == index) {
                    mPage++;
                    getMusicServer(mSingerId, mPage, mLimit);
                }
                listView.setSelectionFromTop(position, view.getHeight() * 4);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void isMusicStateList(List<MusicPlayBean> playBeans) {
        if (playBeans != null && !playBeans.isEmpty()) {
            Logger.d(TAG, "list长度1..." + playBeans.size());
            musicPlayBeans.addAll(playBeans);
            handler.sendEmptyMessage(Search_Music_Success);
        } else {
            handler.sendEmptyMessage(Search_Music_Failure);
        }
    }

    public void onEvent(DataMessage event) {
        Logger.d(TAG, "data.." + event.getData());
        if (event.gettag().equals(TAG)) {
            if (!TextUtils.isEmpty(event.getData())) {
                AJson aJsons = GsonJsonUtils.parseJson2Obj(event.getData(), AJson.class);
                String s = GsonJsonUtils.parseObj2Json(aJsons.getData());
                MusicNumBean numBean = GsonJsonUtils.parseJson2Obj(s, MusicNumBean.class);
                setState(numBean.totalCount);
                isMusicStateList(numBean.list);
            }
        }
    }

    public void setState(String s) {
        Logger.i(TAG, "s.." + s);
        if (!TextUtils.isEmpty(s)) {
            String[] str = (s).split("\\.0");
            try {
                totalCount = Integer.parseInt(str[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
