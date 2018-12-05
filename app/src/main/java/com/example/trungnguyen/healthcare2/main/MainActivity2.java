package com.example.trungnguyen.healthcare2.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.trungnguyen.healthcare2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;

public class MainActivity2 extends AppCompatActivity {
    public static final String TAG = "BasicHistoryApi";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            readHistoryData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                readHistoryData();
            }
        }
    }

    private void readHistoryData() {
        SessionReadRequest readRequest = queryFitnessData();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Fitness.getSessionsClient(this, account)
                .readSession(readRequest)
                .addOnSuccessListener(sessionReadResponse -> {
                    List<Session> sessions = sessionReadResponse.getSessions();
                    Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                            + sessions.size() + " - " + sessionReadResponse.getStatus());

                    for (Session session : sessions) {
                        // Process the session
//                        dumpSession(session);

                        // Process the data sets for this session
                        List<DataSet> dataSets = sessionReadResponse.getDataSet(session);
                        for (DataSet dataSet : dataSets) {
                            dumpDataSet(dataSet);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "Failed to read session");
                });
    }

    public SessionReadRequest queryFitnessData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
//        cal.add(Calendar.DAY_OF_WEEK, -1);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();

        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yy HH:mm");
        Log.i(TAG, "Range Start: " + df2.format(new Date(startTime)));
        Log.i(TAG, "Range End: " + df2.format(new Date(endTime)));

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
//                .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                .read(DataType.TYPE_CALORIES_EXPENDED)
                .enableServerQueries()
                .readSessionsFromAllApps()
                .build();

        return readRequest;
    }

    private static void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.toString());
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yy HH:mm");

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point: ");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + df2.format(new Date(dp.getStartTime(TimeUnit.MILLISECONDS))));
            Log.i(TAG, "\tEnd: " + df2.format(new Date(dp.getEndTime(TimeUnit.MILLISECONDS))));
            Log.i(TAG, "\tEnd2: " + df2.format(new Date(dp.getTimestamp(TimeUnit.MILLISECONDS))));
            boolean isSleep = false;
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                if (dp.getValue(field).toString().equals("72")) {
                    isSleep = true;
                }
                if (field.getName().contains("duration") && isSleep) {
                    Value value = dp.getValue(field);
                    float sleepHours = (float) (Math.round((value.asInt() * 2.778 * 0.0000001 * 10.0)) / 10.0);
                    Log.i(TAG, "\tField: Sleep duration in h " + sleepHours);
                }
            }
        }
    }

    private void dumpSession(Session session) {
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yy HH:mm");
        Log.i("Sessionnnnnnnnn: ", df2.format(new Date(session.getStartTime(TimeUnit.MILLISECONDS)))
                + " - " + df2.format(new Date(session.getEndTime(TimeUnit.MILLISECONDS))));
    }
}
