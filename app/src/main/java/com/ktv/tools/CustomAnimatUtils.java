package com.ktv.tools;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 动画工具类
 */
public class CustomAnimatUtils {
    public static void showStyle1(View view, Context context,int anim,boolean after){
        Animation anima= AnimationUtils.loadAnimation(context,anim);
        anima.setFillAfter(after);//动画执行完后停留在执行完的状态
        view.startAnimation(anima);//执行动画
    }
}
