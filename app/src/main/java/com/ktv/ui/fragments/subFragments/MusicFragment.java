package com.ktv.ui.fragments.subFragments;


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
import com.ktv.adapters.MusicPlayAdaters;
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
import com.ktv.ui.MainActivity;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * (搜索)根据歌名搜索得到歌曲的列表  (2级)
 */
public class MusicFragment extends BaseFr {

    private static final String TAG="MusicFragment";

    private View view;
    private Context mContext;

    private TextView mSerachText;//如果列表有值,则显示 "搜到多少歌曲",反之显示"歌手名称"
    private TextView mNofoundText;

    private ListView listView;
    private MusicPlayAdaters playAdater;
    private List<MusicPlayBean> musicPlayBeans;

    public static final int Search_Music_Success=100;//搜索歌曲成功
    public static final int Search_Music_Failure=200;//搜索歌曲失败

    private WeakHashMap<String,String> weakHashMap=new WeakHashMap<>();

    private int mIndex;
    private String mSearchContent;

    private int mLimit = App.limit;//页码量
    private int mPage = 1;//第几页

    public DbManager mDb;

    public int totalCount=0;


    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Search_Music_Success:
                    mNofoundText.setVisibility(View.GONE);
                    playAdater.notifyDataSetChanged();
                    mSerachText.setText("搜索到歌曲 "+totalCount+" 首");
                    break;
                case Search_Music_Failure:
                    mNofoundText.setVisibility(View.VISIBLE);
                    mNofoundText.setText("未能搜索到您想要的歌曲!");
                    mSerachText.setText("搜索条件: "+mSearchContent);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.music_fragment, container, false);

        ((MainActivity)getActivity()).cleanFocus(false);

        getIntentData();
        mContext=getActivity();
        initView();
        initLiter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        playAdater.notifyDataSetChanged();
    }

    /**
     * Bundle传值
     */
    private void getIntentData(){
        mIndex= getArguments().getInt("mIndex");
        mSearchContent= getArguments().getString("searchContent");
        Logger.i(TAG,"mIndex..."+mIndex+"...mSearchContent..."+mSearchContent);
        getServer(mIndex,mSearchContent,mPage,mLimit);
    }

    /**
     * 接口获取:歌曲列表
     */
    private void getServer(int mIndex,String searchContent,int page,int limit){
        weakHashMap.put("mac", App.mac);
        weakHashMap.put("STBtype","2");
        weakHashMap.put("page",page+"");//第几页    不填默认1
        weakHashMap.put("limit",limit+"");//页码量   不填默认10，最大限度100
        weakHashMap.put("singerId",null);//歌手id
        weakHashMap.put("songnumber",null);//歌曲编号
        switch (mIndex){
            case 1:
                weakHashMap.put("zhuyin",searchContent);//拼音
                break;
            case 2:
                weakHashMap.put("pinyin",searchContent);//注音
                break;
            case 3:
                weakHashMap.put("keyword",searchContent);//keyword 按关键字搜索
                break;
            case 4:
                weakHashMap.put("vietnam",searchContent);//越南
                break;
            case 5:
                weakHashMap.put("japanese",searchContent);//日文
                break;
            default:
                weakHashMap.put("keyword",searchContent);//keyword 按关键字搜索
                break;
        }
        String url= App.getRqstUrl(App.headurl+"song", weakHashMap);
        Logger.i(TAG,"url.."+url);
        Req.get(TAG, url);
    }

    private void initView(){
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        mDb = x.getDb(daoConfig);

        musicPlayBeans=new ArrayList<>();

        mSerachText=view.findViewById(R.id.no_found_text_tvw);
        mNofoundText=view.findViewById(R.id.no_found_tvw);
        listView=view.findViewById(R.id.listview);
        listView.setItemsCanFocus(true);//设置item项的子控件能够获得焦点（默认为false，即默认item项的子空间是不能获得焦点的）

        playAdater=new MusicPlayAdaters(getActivity(),R.layout.music_play_item, musicPlayBeans,mDb);
        listView.setAdapter(playAdater);
    }

    private void initLiter(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.i(TAG,"position.."+position);
                ToastUtils.showShortToast(mContext,"歌名搜索得到的列表position.."+position);
            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = position+1;
                if (mPage*mLimit==index){
                    mPage++;
                    getServer(mIndex,mSearchContent,mPage,mLimit);
                }
                listView.setSelectionFromTop(position, view.getHeight() * 5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void isMusicStateList(List<MusicPlayBean> playBeans){
        if (playBeans !=null&&!playBeans.isEmpty()){
            Logger.d(TAG,"list长度1..."+playBeans.size());
            musicPlayBeans.addAll(playBeans);
            handler.sendEmptyMessage(Search_Music_Success);
        } else {
            handler.sendEmptyMessage(Search_Music_Failure);
        }
    }

    public void onEvent(DataMessage event) {
        Logger.d(TAG,"data.."+event.getData());
        if (event.gettag().equals(TAG)) {
            if(!TextUtils.isEmpty(event.getData())){
                AJson aJsons=  GsonJsonUtils.parseJson2Obj(event.getData(),AJson.class);
                String s=  GsonJsonUtils.parseObj2Json(aJsons.getData());
                MusicNumBean numBean= GsonJsonUtils.parseJson2Obj(s,MusicNumBean.class);
                setState(numBean.totalCount);
                isMusicStateList(numBean.list);
            }
        }
    }

    public void setState(String s){
        Logger.i(TAG,"s.."+s);
        if (!TextUtils.isEmpty(s)){
            String [] str= (s).split("\\.0");
            try {
                totalCount=Integer.parseInt(str[0]);
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
    }
}
