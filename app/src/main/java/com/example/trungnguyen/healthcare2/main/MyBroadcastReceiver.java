package com.example.trungnguyen.healthcare2.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("JRE", "Callback onReceive Intent open URL" + intent.getDataString());
    }
}
