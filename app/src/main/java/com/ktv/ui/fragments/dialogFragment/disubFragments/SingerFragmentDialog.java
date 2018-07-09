package com.ktv.ui.fragments.dialogFragment.disubFragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.google.gson.reflect.TypeToken;
import com.ktv.R;
import com.ktv.adapters.SingerPlayAdater;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.SingerNumBean;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.Logger;
import com.ktv.ui.BaseFr;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * (搜索)根据歌名搜索得到歌手的列表  (2级)
 */
public class SingerFragmentDialog extends BaseFr {

    private static final String TAG="SingerFragmentDialog";

    private View view;
    private Context mContext;

    private TextView mSerachText;//如果列表有值,则显示 "搜到多少歌手",反之显示"歌手名称"
    private TextView mNofoundText;

    private ListView listView;
    private SingerPlayAdater singerPlayAdater;
    private List<SingerNumBean.SingerBean> mSingerBeans;

    public static final int Search_Song_Success=300;//搜索歌手成功
    public static final int Search_Song_Failure=400;//搜索歌手失败

    private FragmentManager manager;
    private FragmentTransaction ft;

    private WeakHashMap<String,String> weakHashMap=new WeakHashMap<>();

    private int mIndex;
    private String mSearchContent;

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Search_Song_Success:
                    mNofoundText.setVisibility(View.GONE);
                    singerPlayAdater.notifyDataSetChanged();
                    mSerachText.setText("搜索到歌手 "+mSingerBeans.size()+" 名");
                    break;
                case Search_Song_Failure:
                    mNofoundText.setVisibility(View.VISIBLE);
                    mNofoundText.setText("未能搜索到您想要的歌手!");
                    mSerachText.setText("搜索条件: "+mSearchContent);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.music_fragment_dialog, container, false);
        getIntentData();
        mContext=getActivity();
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
     * Bundle传值
     */
    private void getIntentData(){
        mIndex= getArguments().getInt("mIndex");
        mSearchContent= getArguments().getString("searchContent");
        Logger.i(TAG,"mIndex..."+mIndex+"...mSearchContent..."+mSearchContent);
        getSingerServer(mIndex,mSearchContent);
    }

    /**
     * 接口获取:歌手列表
     */
    private void getSingerServer(int mIndex,String searchContent){
        weakHashMap.put("mac", App.mac);
        weakHashMap.put("STBtype","2");
        weakHashMap.put("page","1");//第几页    不填默认1
        weakHashMap.put("limit","100");//页码量   不填默认10，最大限度100
        weakHashMap.put("singertypeid",null);//歌手类型id
        switch (mIndex){
            case 1:
                weakHashMap.put("zhuyin",searchContent);//拼音
                break;
            case 2:
                weakHashMap.put("pinyin",searchContent);//注音
                break;
            case 3:
                break;
            case 4:
                weakHashMap.put("vietnam",searchContent);//越南
                break;
            case 5:
                weakHashMap.put("japanese",searchContent);//日文
                break;
        }
        String url= App.getRqstUrl(App.headurl+"song/singer", weakHashMap);
        Logger.i(TAG,"url.."+url);
        Req.get(TAG, url);
    }

    private void initView(){
        mSingerBeans=new ArrayList<>();

        mSerachText=view.findViewById(R.id.no_found_text_tvw);
        mNofoundText=view.findViewById(R.id.no_found_tvw);
        listView=view.findViewById(R.id.listview);

        singerPlayAdater=new SingerPlayAdater(getActivity(),R.layout.singer_play_item_dialog, mSingerBeans);
        listView.setAdapter(singerPlayAdater);
    }

    /**
     * 切换到 MusicSubFragment
     * @param id
     * @param name
     */
    private void toClass(String id,String name){
        Bundle bundle = new Bundle();
        ft = manager.beginTransaction();
        MusicSubFragmentDialog mufrt = new MusicSubFragmentDialog();
        ft.replace(R.id.main_popudows, mufrt);
        bundle.putString("sid",id);
        bundle.putString("sname",name);
        mufrt.setArguments(bundle);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void initLiter(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerNumBean.SingerBean singerBean=  mSingerBeans.get(position);
                toClass(singerBean.id,singerBean.name);
            }
        });
    }

    private void isSingerStateList(List<SingerNumBean.SingerBean> playBeans){
        if (playBeans !=null&&!playBeans.isEmpty()){
            Logger.d(TAG,"list长度2..."+playBeans.size());
            mSingerBeans.addAll(playBeans);
            handler.sendEmptyMessage(Search_Song_Success);
        } else {
            handler.sendEmptyMessage(Search_Song_Failure);
        }
    }

    public void onEvent(DataMessage event) {
        Logger.d(TAG,"data.."+event.getData());
        if (event.gettag().equals(TAG)) {
            if(!TextUtils.isEmpty(event.getData())){
                mSingerBeans.clear();
                AJson aJsons=  GsonJsonUtils.parseJson2Obj(event.getData(),AJson.class);
                String s=  GsonJsonUtils.parseObj2Json(aJsons.getData());
                Logger.d(TAG,"s.."+s);
                List<SingerNumBean.SingerBean> playBeans=GsonJsonUtils.parseJson2Obj(s, new TypeToken<List<SingerNumBean.SingerBean>>(){});
                isSingerStateList(playBeans);
            }
        }
    }
}
