package com.ktv.ui.fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ktv.R;
import com.ktv.adapters.CustomAdater;
import com.ktv.net.Req;
import com.ktv.tools.Constant;
import com.ktv.tools.SoftKeyboard;
import com.ktv.tools.ToastUtils;
import com.ktv.ui.BaseFr;
import com.ktv.ui.MainActivity;
import com.ktv.ui.fragments.subFragments.MusicFragment;
import com.ktv.ui.fragments.subFragments.SingerFragment;

/**
 * (搜索) (1级)
 */
public class Fragment1 extends BaseFr implements View.OnFocusChangeListener {

    private static final String TAG = "Fragment1";

    private TextView mUltinomialSerach;
    private EditText mSerchLanguageEdt;//输入歌曲名称、歌星、即可搜索
    private TextView mSpSinger;
    private ImageView mDeleteClose;


    private RadioButton rdb1_top_menu;
    private RadioButton rdb2_top_menu;
    private RadioButton rdb3_top_menu;
    private RadioButton rdb4_top_menu;
    private RadioButton rdb5_top_menu;

    private GridView gridView;//键盘
    private ImageView delete;//键盘中 X按钮

    private int LanguageType = 0;
    private View view;
    private String[] arrays = null;

    Context mContext;

    private CustomAdater customAdater;

    private boolean isMusicState = true;//标识歌名:true  歌手:false

    private FragmentManager manager;
    private FragmentTransaction ft;

    private int mIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment1, container, false);
        mContext = getActivity();

        ((MainActivity) getActivity()).cleanFocus(true);

        initPart1();
        initPart2();
        initPart3();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String content = mSerchLanguageEdt.getText().toString().trim();
        outState.putString("inputCon", content);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mSerchLanguageEdt.setText(savedInstanceState.getString("inputCon", ""));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getFragmentManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSpSinger.setText(isMusicState ? "歌名" : "歌星");
        mSerchLanguageEdt.requestFocus();
    }

    /**
     * 部分2
     */
    private void initPart1() {
        mDeleteClose = view.findViewById(R.id.delete_close_ivw);
        mUltinomialSerach = view.findViewById(R.id.multinomial_serach);
        mSerchLanguageEdt = view.findViewById(R.id.serch_language_edt);

        SoftKeyboard.hideSoftInputMode(getActivity(), mSerchLanguageEdt);//禁止显示键盘
        mSpSinger = view.findViewById(R.id.spinner_singer);
        mSpSinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPoPuWindos();
            }
        });
        //点击搜索
        mUltinomialSerach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serach = mSerchLanguageEdt.getText().toString().trim();
//                if (TextUtils.isEmpty(serach)){
//                    ToastUtils.showShortToast(mContext,"请先填写关键字");
//                    return;
//                }
//
//                if (serach.contains(".")){
//                    mSerchLanguageEdt.setText(null);
//                    ToastUtils.showShortToast(mContext,"输入框不能包含特殊字符");
//                    return;
//                }

                toClass(isMusicState);
            }
        });

        mSerchLanguageEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LanguageType = 100;
                arrays = Constant.LanguageType.PhoneticNotation;
                customAdater.setData(arrays);
                customAdater.notifyDataSetChanged();
                view.findViewById(R.id.lable6).setVisibility(View.VISIBLE);
            }
        });

        mDeleteClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSerchLanguageEdt.setText(null);
            }
        });
    }

    /**
     * 显示自定义Popuwindows
     */
    private void showPoPuWindos() {
        mSpSinger.setText(null);
        View strView = getActivity().getLayoutInflater().inflate(R.layout.powin_layout, null, false);
        final PopupWindow window = new PopupWindow(strView, 90, 90, true);
        window.setAnimationStyle(R.style.AnimationFade);
        window.setOutsideTouchable(true);
//        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAsDropDown(view.findViewById(R.id.spinner_singer), -6, -40);

        TextView musicName = strView.findViewById(R.id.music_name_tvw);
        TextView singerName = strView.findViewById(R.id.singer_name_tvw);
        musicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpSinger.setText("歌名");
                isMusicState = true;
                window.dismiss();
            }
        });

        singerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpSinger.setText("歌星");
                isMusicState = false;
                window.dismiss();
            }
        });
    }

    /**
     * 部分5
     */
    private void initPart2() {
        rdb1_top_menu = view.findViewById(R.id.rdb1_top_menu);
        rdb2_top_menu = view.findViewById(R.id.rdb2_top_menu);
        rdb3_top_menu = view.findViewById(R.id.rdb3_top_menu);
        rdb4_top_menu = view.findViewById(R.id.rdb4_top_menu);
        rdb5_top_menu = view.findViewById(R.id.rdb5_top_menu);

        rdb1_top_menu.setOnFocusChangeListener(this);
        rdb2_top_menu.setOnFocusChangeListener(this);
        rdb3_top_menu.setOnFocusChangeListener(this);
        rdb4_top_menu.setOnFocusChangeListener(this);
        rdb5_top_menu.setOnFocusChangeListener(this);
    }

    /**
     * 部分6
     */
    private void initPart3() {
        gridView = view.findViewById(R.id.grid);
        gridView.setNumColumns(20);
        customAdater = new CustomAdater(getActivity(), arrays);
        gridView.setAdapter(customAdater);
        delete = view.findViewById(R.id.delete_text);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isState(i);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = mSerchLanguageEdt.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    String s1 = str.substring(0, str.length() - 1);
                    mSerchLanguageEdt.setText(s1);
                } else {
                    ToastUtils.showShortToast(mContext, "输入框不存在字符!");
                }
            }
        });
    }

    private void isState(int postion) {
        String resule = "";
        switch (LanguageType) {
            case 100:
                resule = Constant.LanguageType.PhoneticNotation[postion];
                mIndex = 1;
                break;
            case 200:
                resule = Constant.LanguageType.PinYin[postion];
                mIndex = 2;
                break;
            case 300:
                resule = Constant.LanguageType.Number[postion];
                mIndex = 3;
                break;
            case 400:
                resule = Constant.LanguageType.Vietnam[postion];
                mIndex = 4;
                break;
            case 500:
                resule = Constant.LanguageType.Japanese[postion];
                mIndex = 5;
                break;
        }
        String str = mSerchLanguageEdt.getText().toString() + resule;
        mSerchLanguageEdt.setText(str);
    }

    @Override
    public void onFocusChange(View view, boolean b) {

        switch (view.getId()) {
            case R.id.rdb1_top_menu:
                LanguageType = 100;
                arrays = Constant.LanguageType.PhoneticNotation;
                break;
            case R.id.rdb2_top_menu:
                LanguageType = 200;
                arrays = Constant.LanguageType.PinYin;
                break;
            case R.id.rdb3_top_menu:
                LanguageType = 300;
                arrays = Constant.LanguageType.Number;
                break;
            case R.id.rdb4_top_menu:
                LanguageType = 400;
                arrays = Constant.LanguageType.Vietnam;
                break;
            case R.id.rdb5_top_menu:
                LanguageType = 500;
                arrays = Constant.LanguageType.Japanese;
                break;
        }
        customAdater.setData(arrays);
        customAdater.notifyDataSetChanged();

    }

    /**
     * 根据 isMusicState区分,是通过歌名搜索,还是通过歌星搜索
     *
     * @param isMusicState
     */
    @SuppressWarnings("ResourceType")
    private void toClass(boolean isMusicState) {
        Bundle bundle = new Bundle();
        bundle.putInt("mIndex", mIndex);
        bundle.putString("searchContent", mSerchLanguageEdt.getText().toString().trim());
        ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.back_left_in, R.anim.back_right_out);//翻转来回
        if (isMusicState) {
            MusicFragment mufrt = new MusicFragment();
            ft.replace(R.id.main, mufrt);
            mufrt.setArguments(bundle);
        } else {
            SingerFragment sifrt = new SingerFragment();
            ft.replace(R.id.main, sifrt);
            sifrt.setArguments(bundle);
        }
        ft.addToBackStack(null);
        ft.commit();
    }


}
