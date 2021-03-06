package com.ktv.ui.fragments.subFragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.CustomAdater;
import com.ktv.adapters.SingerTypeAdapter;
import com.ktv.adapters.base.RecyclerAdapter;
import com.ktv.adapters.base.SpacesItemDecoration;
import com.ktv.app.App;
import com.ktv.bean.AJson;
import com.ktv.bean.SingerNumBean;
import com.ktv.event.DataMessage;
import com.ktv.net.Req;
import com.ktv.tools.Constant;
import com.ktv.tools.GsonJsonUtils;
import com.ktv.tools.Logger;
import com.ktv.tools.SoftKeyboard;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.BaseFr;
import com.ktv.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 歌星分类(点歌台) (大陆男歌星,内地女歌星,港台女歌星,国语合唱)  2级
 */
public class SingerTypeFragment extends BaseFr {
    private static final String TAG = "SingerTypeFragment";

    private View view;
    private Context mContext;

    private RecyclerView mRecyclerView;
    private SingerTypeAdapter playAdater;
    private List<SingerNumBean.SingerBean> mItemList;

    private WeakHashMap<String, String> weakHashMap = new WeakHashMap<>();

    public static final int Search_Music_Success = 100;//查找歌曲歌曲成功
    public static final int Search_Music_Failure = 200;//查找歌曲失败

    private StaggeredGridLayoutManager mLayoutManager;

    private TextView mTextLeft;//显示列表名称(如:大中华,内地男歌星,内地女歌星...)

    private EditText mSerch;//输入框

    private TextView mListText;//键盘选项

    private TextView mNoText;//无数据提示

    private FragmentManager manager;
    private FragmentTransaction ft;

    private String mSingerId;//歌曲大类ID
    private String mSingerName;//歌曲大类名称

    private int mSetTextName = Constant.InputNameMethod.InputNameOne;//动态选择文字

    private String[] arrays = null;

    private CustomAdater mAdater;

    private int mLimit = App.Srclimit;//页码量
    private int mPage = 1;//第几页

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Search_Music_Success:
                    mTextLeft.setText(mSingerName);//显示大类列表名称
                    mNoText.setVisibility(View.GONE);
                    playAdater.notifyDataSetChanged();
                    break;
                case Search_Music_Failure:
                    mTextLeft.setText(mSingerName);//显示大类列表名称
                    playAdater.notifyDataSetChanged();
                    mNoText.setVisibility(View.VISIBLE);
                    mNoText.setText("當前無歌星分類");
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment2, container, false);
        mContext = getActivity();

        ((MainActivity)getActivity()).cleanFocus(false);

        initView();
        initLiter();
        getIntentData();
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
    private void getIntentData() {
        mSingerId = getArguments().getString("singerId");
        mSingerName = getArguments().getString("singerName");

        Logger.i(TAG, "mSingerId..." + mSingerId + "...mSingerName..." + mSingerName);
        mPage=1;
        getMusicServer(false);
    }

    /**
     * 初始化View
     */
    private void initView() {
        mItemList=new ArrayList<>();

        mNoText = view.findViewById(R.id.no_tvw);

        mTextLeft = view.findViewById(R.id.textLeft_tvw);
        mSerch = view.findViewById(R.id.serch_edt);
        SoftKeyboard.hideSoftInputMode(getActivity(), mSerch);//禁止显示键盘
        mListText = view.findViewById(R.id.list_tvw);

        mRecyclerView = view.findViewById(R.id.recyview);

        playAdater = new SingerTypeAdapter(getActivity(), R.layout.adapter_grid, mItemList);
        mRecyclerView.setAdapter(playAdater);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 30, 0, 40));
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /**
     * item事件
     */
    private void initLiter() {
        playAdater.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SingerNumBean.SingerBean singerBean = mItemList.get(position);
                toClass(singerBean.id, singerBean.name);
            }
        });

        mListText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPoPuWindos();
            }
        });

        mSerch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputPown();
            }
        });

        playAdater.setOnItemSelectedListener(new RecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onFocusChange(View view, int position) {
                Logger.i(TAG,"position...."+position);
                int index = position+1;
                if (mPage*mLimit==index){
                    mPage++;
                    getMusicServer(false);
                }
            }
        });
    }

    private void Attribute(View strView, final PopupWindow window) {
        ImageView serach = strView.findViewById(R.id.serach_ivw);//搜索键
        serach.setVisibility(View.VISIBLE);
        ImageView delete = strView.findViewById(R.id.delete_text);//清除键
        GridView gridView = strView.findViewById(R.id.grid);//键盘
        gridView.setNumColumns(10);
        mAdater = new CustomAdater(getActivity(), arrays);
        gridView.setAdapter(mAdater);
        mAdater.notifyDataSetChanged();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isState(i);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = mSerch.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    String s1 = str.substring(0, str.length() - 1);
                    mSerch.setText(s1);
                    mSerch.setSelection(mSerch.getText().length());
                } else {
                    ToastUtils.showShortToast(mContext, "输入框不存在字符!");
                }
            }
        });

        serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seachText=mSerch.getText().toString().trim();
                if (TextUtils.isEmpty(seachText)) {
                    ToastUtils.showShortToast(mContext, "请先选择搜索关键字");
                    return;
                }

                if (seachText.contains(".")){
                    mSerch.setText(null);
                    ToastUtils.showShortToast(mContext,"输入框不能包含特殊字符");
                    return;
                }
                window.dismiss();
                mItemList.clear();
                getMusicServer(true);
            }
        });
    }

    private void isState(int postion) {
        String resule = "";
        switch (mSetTextName) {
            case Constant.InputNameMethod.InputNameOne:
                resule = Constant.LanguageType.PhoneticNotation[postion];
                break;
            case Constant.InputNameMethod.InputNameTwo:
                resule = Constant.LanguageType.PinYin[postion];
                break;
            case Constant.InputNameMethod.InputNameThree:
                resule = Constant.LanguageType.Number[postion];
                break;
            case Constant.InputNameMethod.InputNameFour:
                resule = Constant.LanguageType.Vietnam[postion];
                break;
            case Constant.InputNameMethod.InputNameFive:
                resule = Constant.LanguageType.Japanese[postion];
                break;
        }
        String str = mSerch.getText().toString() + resule;
        mSerch.setText(str);
        mSerch.setSelection(mSerch.getText().length());
    }

    /**
     * 显示键盘输入法
     */
    private void showInputPown() {
        weakHashMap.clear();
        int y = -84;

        switch (mSetTextName) {
            case Constant.InputNameMethod.InputNameOne:
                y = -84;
                arrays = Constant.LanguageType.PhoneticNotation;
                break;
            case Constant.InputNameMethod.InputNameTwo:
                y = -105;
                arrays = Constant.LanguageType.PinYin;
                break;
            case Constant.InputNameMethod.InputNameThree:
                y = -145;
                arrays = Constant.LanguageType.Number;
                break;
            case Constant.InputNameMethod.InputNameFour:
                y = -104;
                arrays = Constant.LanguageType.Vietnam;
                break;
            case Constant.InputNameMethod.InputNameFive:
                y = -65;
                arrays = Constant.LanguageType.Japanese;
                break;
        }

        View strView = getActivity().getLayoutInflater().inflate(R.layout.show_keyboard, null, false);

        final PopupWindow window = new PopupWindow(strView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setAnimationStyle(R.style.AnimationFade);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAtLocation(mListText, Gravity.RIGHT | Gravity.CENTER, 160, y);
        Attribute(strView, window);
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (mSetTextName) {
            case Constant.InputNameMethod.InputNameOne:
                mListText.setText("注 音");
                break;
            case Constant.InputNameMethod.InputNameTwo:
                mListText.setText("拼 音");
                break;
            case Constant.InputNameMethod.InputNameThree:
                mListText.setText("数 字");
                break;
            case Constant.InputNameMethod.InputNameFour:
                mListText.setText("越 南");
                break;
            case Constant.InputNameMethod.InputNameFive:
                mListText.setText("日 文");
                break;
        }
        if (mRecyclerView!=null){
            mRecyclerView.requestFocus();
        }
    }

    /**
     * 显示自定义Popuwindows
     */
    private void showPoPuWindos() {
        mSerch.setText(null);
        mListText.setText(null);
        View strView = getActivity().getLayoutInflater().inflate(R.layout.serch_powin_layout, null, false);
        final PopupWindow window = new PopupWindow(strView, 90, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setAnimationStyle(R.style.AnimationFade);
        window.setOutsideTouchable(true);
//        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAsDropDown(view.findViewById(R.id.list_tvw), -6, -40);

        final TextView textType1 = strView.findViewById(R.id.text_type_1);
        final TextView textType2 = strView.findViewById(R.id.text_type_2);
        final TextView textType3 = strView.findViewById(R.id.text_type_3);
        final TextView textType4 = strView.findViewById(R.id.text_type_4);
        final TextView textType5 = strView.findViewById(R.id.text_type_5);

        textType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListText.setText(null);
                mListText.setText(textType1.getText().toString().trim());
                mSetTextName = Constant.InputNameMethod.InputNameOne;
                window.dismiss();
            }
        });

        textType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListText.setText(null);
                mListText.setText(textType2.getText().toString().trim());
                mSetTextName = Constant.InputNameMethod.InputNameTwo;
                window.dismiss();
            }
        });

        textType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListText.setText(null);
                mListText.setText(textType3.getText().toString().trim());
                mSetTextName = Constant.InputNameMethod.InputNameThree;
                window.dismiss();
            }
        });

        textType4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListText.setText(null);
                mListText.setText(textType4.getText().toString().trim());
                mSetTextName = Constant.InputNameMethod.InputNameFour;
                window.dismiss();
            }
        });

        textType5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListText.setText(null);
                mListText.setText(textType5.getText().toString().trim());
                mSetTextName = Constant.InputNameMethod.InputNameFive;
                window.dismiss();
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
                    SingerNumBean numBean = GsonJsonUtils.parseJson2Obj(s, SingerNumBean.class);
                    isMusicStateList(numBean.list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void isMusicStateList(List<SingerNumBean.SingerBean> playBeans) {
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

    public void getMusicServer(boolean isState){
        weakHashMap.clear();
        weakHashMap.put("mac", App.mac);
        weakHashMap.put("STBtype", "2");
        weakHashMap.put("page", mPage+"");//第几页    不填默认1
        weakHashMap.put("limit", mLimit+"");//页码量   不填默认10，最大限度100
        weakHashMap.put("songtypeid", mSingerId);//歌手类型id

        if (isState){
            switch (mSetTextName) {
                case Constant.InputNameMethod.InputNameOne:
                    weakHashMap.put("zhuyin", mSerch.getText().toString().trim());//拼音
                    break;
                case Constant.InputNameMethod.InputNameTwo:
                    weakHashMap.put("pinyin", mSerch.getText().toString().trim());//注音
                    break;
                case Constant.InputNameMethod.InputNameThree:
                    weakHashMap.put("keyword",mSerch.getText().toString().trim());//keyword 按关键字搜索
                    break;
                case Constant.InputNameMethod.InputNameFour:
                    weakHashMap.put("vietnam", mSerch.getText().toString().trim());//越南
                    break;
                case Constant.InputNameMethod.InputNameFive:
                    weakHashMap.put("japanese", mSerch.getText().toString().trim());//日文
                    break;
                default:
                    weakHashMap.put("keyword",mSerch.getText().toString().trim());//keyword 按关键字搜索
                    break;
            }
        }

        String url = App.getRqstUrl(App.headurl + "song/getsongSingerType", weakHashMap);
        Logger.i(TAG, "url.." + url);
        Req.get(TAG, url);
    }

    /**
     * 切换到 歌星界面
     *
     * @param id
     * @param name
     */
    private void toClass(String id, String name) {
        Bundle bundle = new Bundle();
        ft = manager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);//打开
        SingerListFragment mufrt = new SingerListFragment();
        ft.replace(R.id.main, mufrt);
        bundle.putString("singertypeid", id);
        bundle.putString("singerTypeName", name);
        mufrt.setArguments(bundle);
        ft.addToBackStack(null);
        ft.commit();
    }
}
