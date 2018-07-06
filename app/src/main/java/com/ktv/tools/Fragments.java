package com.ktv.tools;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.ktv.R;
import com.ktv.app.App;

public class Fragments {
    private static FragmentTransaction ft;
    private static int fragmentid = R.id.main;

    public static void replace(FragmentManager fm, Fragment fragment) {
        if (fragment != null) {
            ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);//打开
            ft.replace(fragmentid, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

    }
    public static void add(FragmentManager fm, Fragment fragment) {
        if (fragment != null) {
            ft = fm.beginTransaction();
            ft.addToBackStack(null);
            ft.replace(fragmentid, fragment);
            ft.commit();
        }

    }

    public static void hideFragment(App app, Fragment fragment, FragmentTransaction fragmentTransaction) {
        //如果此fragment不为空的话就隐藏起来
        if (fragment != null) {
            fragmentTransaction.hide(fragment).commit();
        }
    }

}
