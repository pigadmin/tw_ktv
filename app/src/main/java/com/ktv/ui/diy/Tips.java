package com.ktv.ui.diy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Tips {
    public static AlertDialog show(Context context, String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
        return dialog;
    }
}
