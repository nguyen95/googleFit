package com.example.trungnguyen.healthcare2.main;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.trungnguyen.healthcare2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateListenerRegistrationRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;
import static timber.log.Timber.e;

public class MainActivity3 extends AppCompatActivity {
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private GoogleApiClient mClient, mClient2;
    RecyclerView mRecyclerView;
    static FitAdapter fitAdapter;
    static ArrayList<DataSet> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rc_view);
        data.clear();
        fitAdapter = new FitAdapter(data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(fitAdapter);

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_WRITE)
                .addDataType(HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit();
            }
        }
    }

    private void accessGoogleFit() {
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        getData(account);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("hihihihihi: ", intent.getDataString());
    }

    private void getData(GoogleSignInAccount account) {
        getDataWithType(account, DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA);
        getDataWithType(account, DataType.TYPE_WEIGHT, DataType.AGGREGATE_WEIGHT_SUMMARY);
        getDataWithType(account, DataType.TYPE_BODY_FAT_PERCENTAGE, DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY);
        getDataWithType(account, DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY);
        getDataWithType(account, HealthDataTypes.TYPE_BLOOD_PRESSURE, HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY);
        getDataWithType(account, DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY);
    }

    private void getDataWithType(GoogleSignInAccount account, DataType type1, DataType type2) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms").build();

        DataReadRequest readRequest = type1 == DataType.TYPE_STEP_COUNT_DELTA
                ? new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS, type2)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
                : new DataReadRequest.Builder()
                .aggregate(type1, type2)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        insertFitnessData(account, readRequest);
    }

    private void printData(DataReadResponse dataReadResponse) {
        if (dataReadResponse.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResponse.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                data.addAll(dataSets);
                fitAdapter.notifyDataSetChanged();
            }
        } else if (dataReadResponse.getDataSets().size() > 0) {
            data.addAll(dataReadResponse.getDataSets());
            fitAdapter.notifyDataSetChanged();
        }
    }

    private void insertFitnessData(GoogleSignInAccount account, DataReadRequest readRequest) {
//        e("Creating a new data insert request.");
//
//        // [START build_insert_data_request]
//        // Set a start and end time for our data, using a start time of 1 hour before this moment.
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        cal.add(Calendar.DAY_OF_WEEK, -3);
//        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.HOUR_OF_DAY, -4);
//        long startTime = cal.getTimeInMillis();
//
//        // Create a data source
//        DataSource dataSource = new DataSource.Builder()
//                .setAppPackageName(this)
//                .setDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
//                .setStreamName("BODY - FAT")
//                .setType(DataSource.TYPE_RAW)
//                .build();
//
//        // Create a data set
//        float fat = 95;
//        DataSet dataSet = DataSet.create(dataSource);
//        // For each data point, specify a start time, end time, and the data value -- in this case,
//        // the number of new steps.
//        DataPoint dataPoint = dataSet.createDataPoint()
//                .setTimestamp(startTime, TimeUnit.MILLISECONDS);
////                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
//        dataPoint.getValue(Field.FIELD_PERCENTAGE).setFloat(fat);
//        dataSet.add(dataPoint);
//        // [END build_insert_data_request]

//        Fitness.getHistoryClient(this, account)
//                .insertData(dataSet)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Fitness.getHistoryClient(getApplicationContext(), account)
//                                .readData(readRequest)
//                                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
//                                    @Override
//                                    public void onSuccess(DataReadResponse dataReadResponse) {
//                                        printData(dataReadResponse);
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        e("onFailure() " + e);
//                                    }
//                                });
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        e("loi roi........");
//                    }
//                });

        Fitness.getHistoryClient(getApplicationContext(), account)
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        printData(dataReadResponse);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e("onFailure() " + e);
                    }
                });
    }
}
