package com.ktv.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ktv.R;
import com.ktv.ui.BaseFr;
import com.ktv.ui.MainActivity;

/**
 * 隔空对唱
 */
public class Fragment5 extends BaseFr {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment5, container, false);
         initView();

        ((MainActivity)getActivity()).cleanFocus(true);

         return view;
    }

    private void initView(){

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
