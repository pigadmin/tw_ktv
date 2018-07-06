package com.ktv.tools;

import android.content.Context;

/**
 *
 *
 * @time 2016/9/5 17:55
 *
 */
public class DensityUtil {

        public static int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
}
