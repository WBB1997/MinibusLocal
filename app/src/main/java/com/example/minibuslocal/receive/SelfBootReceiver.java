package com.example.minibuslocal.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.minibuslocal.activity.LoginActivity;

public class SelfBootReceiver extends BroadcastReceiver {
    private static final String TAG = "SelfBootReceiver";
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION)){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
