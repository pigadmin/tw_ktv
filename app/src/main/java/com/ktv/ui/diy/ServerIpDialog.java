package com.ktv.ui.diy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import com.ktv.R;
import com.ktv.app.App;

public class ServerIpDialog {
    Activity activity;
    Handler handler;

    public ServerIpDialog(Handler handler, Activity activity) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.handler = handler;
    }

    static AlertDialog serverip_dialog;

    public void crt() {
        // TODO Auto-generated method stub
        try {
            serverip_dialog = new AlertDialog.Builder(activity).create();
            if (serverip_dialog != null && serverip_dialog.isShowing()) {
                serverip_dialog.dismiss();
            } else {
                serverip_dialog.setView(new EditText(activity));
                serverip_dialog.show();
            }
            serverip_dialog.setContentView(R.layout.dialog_serverip);
            final EditText serverip = serverip_dialog
                    .findViewById(R.id.serverip);
            serverip.setText(App.ip());
            serverip.setSelection(serverip.length());
            Button serverip_ok = serverip_dialog
                    .findViewById(R.id.serverip_ok);
            serverip_ok.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    App.setip(serverip.getText().toString());
                    restart();
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void restart() {
        final Intent intent = activity.getPackageManager()
                .getLaunchIntentForPackage(activity.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void close() {
        if (serverip_dialog != null && serverip_dialog.isShowing()) {
            serverip_dialog.dismiss();

        }
    }

}
