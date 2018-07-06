package com.ktv.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;

import com.ktv.R;

/**
 * AlertDialog工具类
 */
public class AlertDialogHelper {

    /**
     * @param _Context         文件上下文
     * @param Title            提示
     * @param msg              内容
     * @param icDialogInfo     图片
     * @param onClickListener  确定键
     * @param onClickListeners 取消键
     */
    public static void AlertMsg(Context _Context, String Title, String msg, int icDialogInfo,
                                DialogInterface.OnClickListener onClickListener,
                                DialogInterface.OnClickListener onClickListeners) {
        new AlertDialog.Builder(_Context).setTitle(Title).setCancelable(false).setIcon(icDialogInfo)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return false;
                    }
                }).setMessage(msg).setPositiveButton("确定", onClickListener).setNegativeButton("取消", onClickListeners)
                .show();
    }

    /**
     * @param _Context        文件上下文
     * @param Title           提示
     * @param msg             内容
     * @param icDialogInfo    图片
     * @param onClickListener 确定键
     */
    public static void AlertPositiveMsg(Context _Context, String Title, String msg, int icDialogInfo,
                                        DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(_Context).setTitle(Title).setMessage(msg).setIcon(icDialogInfo).setCancelable(false)
                .setPositiveButton("确定", onClickListener).show();

    }

    /**
     * @param dialog          dialog
     * @param isState         方法是否暴露下外部
     * @param confirmlistener 确定事件
     * @param cancelistener   取消事件
     */
    public static void BtmDialogDerive1(final BtmDialog dialog, boolean isState, View.OnClickListener confirmlistener, View.OnClickListener cancelistener) {
        if (isState) {
            dialog.cancel.setOnClickListener(cancelistener);
        } else {
            dialog.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        dialog.setCancelable(true);
        dialog.confirm.setOnClickListener(confirmlistener);
        dialog.show();
    }
}
