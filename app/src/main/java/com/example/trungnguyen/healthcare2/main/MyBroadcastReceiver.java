package com.example.trungnguyen.healthcare2.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.fitness.data.DataUpdateNotification;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DataUpdateNotification notification = DataUpdateNotification.getDataUpdateNotification(intent);
        Log.e("JRE", "Callback onReceive Intent open URL: " + intent.toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");
        Log.e("JRE", "Callback onReceive Intent data noti: " + dateFormat.format(notification.getUpdateStartTime(TimeUnit.MILLISECONDS)));
        Log.e("JRE", "Callback onReceive Intent data noti: " + notification.toString());
    }
}
