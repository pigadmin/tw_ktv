package com.ktv.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ktv.app.App;
import com.ktv.event.DataMessage;

import de.greenrobot.event.EventBus;

public class BaseFr extends Fragment {
    public static List<Fragment> fragments = new ArrayList<Fragment>();
    private App app;
    private Activity activity;
    private String tag = "BaseFr";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        activity = getActivity();
        app = (App) activity.getApplication();
        fragments.add(this);
    }

    @Override
    public void onDestroyView() {
        fragments.remove(this);
        super.onDestroyView();
    }

    public void onEvent(DataMessage event) {
    }


//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        EventBus.getDefault().unregister(this);
//
//        View v = activity.getWindow().getCurrentFocus();
//        Log.d(tag, "保存焦点\n" + v);
//        if (v != null) {
//            app.getcFcous().add(v);
//        }
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        List<View> v = app.getcFcous();
//        if (!v.isEmpty()) {
//            Log.d(tag, "恢复焦点\n" + v.get(0));
//            v.get(0).requestFocus();
//            app.getcFcous().remove(0);
//        } else {
//            Log.d(tag, "暂无保存焦点");
//        }
//
//    }
}
