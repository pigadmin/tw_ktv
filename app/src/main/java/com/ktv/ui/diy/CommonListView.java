package com.ktv.ui.diy;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ListView;

public class CommonListView extends ListView {


    public CommonListView(Context context) {
        super(context);
        setChildrenDrawingOrderEnabled(true);
    }

    public CommonListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    public boolean isInTouchMode() {
        if (Build.VERSION.SDK_INT == 19) {
            return !(hasFocus() && !super.isInTouchMode());
        }
        return super.isInTouchMode();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int position = getSelectedItemPosition() - getFirstVisiblePosition();
        if (position < 0) {
            return i;
        } else {
            if (i == childCount - 1) {
                if (position > i) {
                    position = i;
                }
                return position;
            }
            if (i == position) {
                return childCount - 1;
            }
        }
        return i;
    }

}