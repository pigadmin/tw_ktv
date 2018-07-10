package com.ktv.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ktv.ui.WelcomeActivity;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // context.startActivity(new Intent(context, WelcomeActivity.class)
            // .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            context.startActivity(new Intent(context, WelcomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
        }
    }

}
