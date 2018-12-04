package com.example.trungnguyen.healthcare2.main;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.trungnguyen.healthcare2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataUpdateListenerRegistrationRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    public static final String TAG = "ServiceFit";
    private GoogleApiClient mClient, mClient2;
    private ServiceCallbacks serviceCallbacks;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MyBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Fitness.HistoryApi.registerDataUpdateListener(MainActivity4.getGoogleApiClient(),
                new DataUpdateListenerRegistrationRequest.Builder()
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setPendingIntent(pendingIntent)
                        .build())
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        if (status.getStatusCode()
                                == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                            Log.i(TAG, "Existing subscription for activity detected.");
                        } else {
                            Log.i(TAG, "Successfully subscribed - update!");
                        }
                    } else {
                        Log.i(TAG, "There was a problem subscribing - update: " + status.toString());
                    }
                });

        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(v ->
                        Log.e(TAG, "Successfully subscribed!")
                )
                .addOnFailureListener(e ->
                        Log.e(TAG, "There was a problem subscribing.")
                );

        OnDataPointListener mListener =
                dataPoint -> {
                    for (Field field : dataPoint.getDataType().getFields()) {
                        Value val = dataPoint.getValue(field);
                        Notification n = new Notification.Builder(getApplication())
                                .setContentTitle("fitness")
                                .setContentText("field: " + field.getName() + "\n"
                                        + "value: " + val)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setAutoCancel(true).build();

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notificationManager.notify(0, n);
                    }
                };

        Fitness.getSensorsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .add(
                        new SensorRequest.Builder()
                                .setDataType(DataType.AGGREGATE_STEP_COUNT_DELTA) // Can't be omitted.
                                .setSamplingRate(10, TimeUnit.SECONDS)
                                .build(),
                        mListener)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "Listener registered!");
                            } else {
                                Log.e(TAG, "Listener not registered.", task.getException());
                            }
                        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
