package com.example.trungnguyen.healthcare2.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

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
        DataReadRequest readRequest = queryFitnessData();
        SessionInsertRequest insertRequest = sessionInsert();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

//        Fitness.getSessionsClient(this, account)
//                .insertSession(insertRequest)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Fitness.getHistoryClient(getApplicationContext(), account)
//                                .readData(readRequest)
//                                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
//                                    @Override
//                                    public void onSuccess(DataReadResponse dataReadResponse) {
//                                        // Get a list of the sessions that match the criteria to check the result.
//                                        printData(dataReadResponse);
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.i(TAG, "Failed to read session");
//                                    }
//                                });
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "Lo roi.......");
//                    }
//                });

        Fitness.getHistoryClient(this, account)
                .readData(queryFitnessData())
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        printData(dataReadResponse);
                    }
                });
    }

    public DataReadRequest queryFitnessData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_WEIGHT, DataType.AGGREGATE_WEIGHT_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
    }

    public SessionInsertRequest sessionInsert() {
        DataSource activitySegmentDataSource = new DataSource.Builder()
                .setAppPackageName(this.getPackageName())
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setName("aaa" + "-activity segments")
                .setType(DataSource.TYPE_RAW)
                .build();
        DataSet activitySegments = DataSet.create(activitySegmentDataSource);

        Calendar cal2 = Calendar.getInstance();
        Date now2 = new Date();
        cal2.setTime(now2);
        cal2.add(Calendar.HOUR_OF_DAY, -2);
        long endTime2 = cal2.getTimeInMillis();
        cal2.add(Calendar.HOUR_OF_DAY, -8);
        long startTime2 = cal2.getTimeInMillis();

        DataPoint firstRunningDp = activitySegments.createDataPoint()
                .setTimeInterval(startTime2, endTime2, TimeUnit.MILLISECONDS);
        firstRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.SLEEP);
        activitySegments.add(firstRunningDp);

// Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName("sleep")
                .setDescription("Long run around Shoreline Park")
                .setIdentifier("UniqueIdentifierHere")
                .setActivity(FitnessActivities.SLEEP)
                .setStartTime(startTime2, TimeUnit.MILLISECONDS)
                .setEndTime(endTime2, TimeUnit.MILLISECONDS)
                .build();

// Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addDataSet(activitySegments)
                .build();

        return insertRequest;
    }


    public static void printData(DataReadResponse dataReadResult) {
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
    }

    private static void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
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
                if(dp.getValue(field).toString().equals("72")){
                    isSleep = true;
                }
                if(field.getName().contains("duration") && isSleep){
                    Value value = dp.getValue(field);
                    float sleepHours  = (float) (Math.round((value.asInt() * 2.778 * 0.0000001*10.0))/10.0);
                    Log.i(TAG, "\tField: Sleep duration in h " + sleepHours);
                }
            }
        }
    }

    private void dumpSession(Session session) {
        Log.i(TAG, session.toString());
    }
}
