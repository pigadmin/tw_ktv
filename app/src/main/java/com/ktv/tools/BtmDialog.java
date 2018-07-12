package com.ktv.tools;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ktv.R;

/**
 * 自定义提示框
 */
public class BtmDialog extends Dialog {

    public TextView cancel;
    public TextView confirm;
    public TextView mTitle;
    public TextView mMessage;


    public BtmDialog(Context context,String title,String message) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.custom_alertdiaog);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        initView(title,message);
    }

    private void initView(String title,String message) {
        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);
        mTitle = findViewById(R.id.title_tv);
        mMessage = findViewById(R.id.message_tv);
        mTitle.setText(title);
        mMessage.setText(message);
    }

    public BtmDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BtmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * 设置 1.取消按钮是否显示 2.更换确定按钮的背景改成圆角
     *
     * @param isCancel
     */
    public void isCancel(boolean isCancel) {
        cancel.setVisibility(isCancel ? View.VISIBLE : View.GONE);
        confirm.setBackgroundResource(R.drawable.selector_back3_shape);
    }
}
