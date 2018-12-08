package com.example.googlefitkit.googleFitEntities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.data.HealthFields;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FitUtil {

    public static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 9009;

    public static FitnessOptions getFitnessOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .build();
    }

    public static boolean isSignInGoogleFit(@NonNull Context context,
                                            @NonNull FitnessOptions fitnessOptions) {
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions);
    }

    public static void requestPermissionsGoogleFit(@NonNull Activity activity,
                                                   @NonNull FitnessOptions fitnessOptions,
                                                   @NonNull OnAccessListener onAccessListener) {
        if (!isSignInGoogleFit(activity, fitnessOptions)) {
            GoogleSignIn.requestPermissions(activity, GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);
        } else {
            ///kokokokoooooooo
            onAccessListener.onAccessGoogleFit();
        }
    }

    public static DataReadRequest queryFitnessData(long endTime, long startTime) {
        return new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                .read(DataType.TYPE_HEART_RATE_BPM)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
    }

    public static DataReadRequest queryFitnessStepData(long endTime, long startTime) {
        DataSource dataSourceStep = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms").build();
        return new DataReadRequest.Builder()
                .aggregate(dataSourceStep, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();
    }

    public static DataReadRequest queryFitnessSleepData(long endTime, long startTime) {
        return new DataReadRequest.Builder()
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
    }

    public static ArrayList<DataSet> dataProcessing(DataReadResponse dataReadResponse) {
        ArrayList<DataSet> dataSets = new ArrayList<>();
        if (dataReadResponse.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResponse.getBuckets()) {
                for (DataSet dataSet : bucket.getDataSets()) dataSets.add(dataSet);
            }
        } else if (dataReadResponse.getDataSets().size() > 0) {
            dataSets.addAll(dataReadResponse.getDataSets());
        }
        return dataSets;
    }

    public static <T> ArrayList<T> parseDataSet(ArrayList<DataSet> dataSets) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        ArrayList<T> datas = new ArrayList<>();

        for (DataSet dataSet : dataSets) {
            List<DataPoint> dataPoints = dataSet.getDataPoints();
            if (dataSet.getDataType().equals(DataType.TYPE_ACTIVITY_SEGMENT)) {
                for (DataPoint dp : dataPoints) {
                    datas.add((T) parseDataPointSleep(dp, datetimeFormat));
                }
            } else if(dataSet.getDataType().equals(DataType.TYPE_WEIGHT)) {
                if (dataPoints != null && dataPoints.size() > 0) {
                    DataPoint dp = dataPoints.get(dataPoints.size() - 1);
                    datas.add((T) parseDataPoint(dp, dateFormat));
                }
            } else if(dataSet.getDataType().equals(DataType.TYPE_BODY_FAT_PERCENTAGE)) {
                if (dataPoints != null && dataPoints.size() > 0) {
                    DataPoint dp = dataPoints.get(dataPoints.size() - 1);
                    datas.add((T) parseDataPoint(dp, dateFormat));
                }
            } else if(dataSet.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)) {
                if (dataPoints != null && dataPoints.size() > 0) {
                    DataPoint dp = dataPoints.get(dataPoints.size() - 1);
                    datas.add((T) parseDataPoint(dp, dateFormat));
                }
            } else if(dataSet.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)) {
                if (dataPoints != null && dataPoints.size() > 0) {
                    DataPoint dp = dataPoints.get(dataPoints.size() - 1);
                    datas.add((T) parseDataPoint(dp, dateFormat));
                }
            } else if(dataSet.getDataType().equals(HealthDataTypes.TYPE_BLOOD_PRESSURE)) {
                if (dataPoints != null && dataPoints.size() > 0) {
                    DataPoint dp = dataPoints.get(dataPoints.size() - 1);
                    datas.add((T) parseDataPoint(dp, dateFormat));
                }
            }
        }
        datas.removeAll(Collections.singleton(null));
        return datas;
    }

    public static <T> T parseDataPoint(DataPoint dp, SimpleDateFormat dateFormat) {
        String startTime = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
        if (dp.getDataType().equals(DataType.TYPE_WEIGHT)) {
            float value = 0;
            for (Field field : dp.getDataType().getFields()) {
                if (field.equals(Field.FIELD_WEIGHT)) {
                    value = dp.getValue(field).asFloat();
                }
            }
            return (T) new GoogleFitData1(startTime, value);
        }
        if (dp.getDataType().equals(DataType.TYPE_BODY_FAT_PERCENTAGE)) {
            float value = 0;
            for (Field field : dp.getDataType().getFields()) {
                if (field.equals(Field.FIELD_PERCENTAGE)) {
                    value = dp.getValue(field).asFloat();
                }
            }
            return (T) new GoogleFitData1(startTime, value);
        }
        if (dp.getDataType().equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
            int value = 0;
            for (Field field : dp.getDataType().getFields()) {
                if (field.equals(Field.FIELD_STEPS)) {
                    value = dp.getValue(field).asInt();
                }
            }
            return (T) new GoogleFitData1(startTime, value);
        }
        if (dp.getDataType().equals(HealthDataTypes.TYPE_BLOOD_PRESSURE)) {
            float systolic = 0, diasolic = 0;
            for (Field field : dp.getDataType().getFields()) {
                if (field.equals(HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC)) {
                    systolic = dp.getValue(field).asFloat();
                }
                if (field.equals(HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC)) {
                    diasolic = dp.getValue(field).asFloat();
                }
            }
            return (T) new GoogleFitDataBloodPressure(startTime, systolic, diasolic);
        }
        if (dp.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)) {
            int value = 0, min = 0, max = 0;
            for (Field field : dp.getDataType().getFields()) {
                if (field.equals(Field.FIELD_BPM)) {
                    value = dp.getValue(field).asInt();
                }
                if (field.equals(Field.FIELD_MIN)) {
                    min = dp.getValue(field).asInt();
                }
                if (field.equals(Field.FIELD_MAX)) {
                    max = dp.getValue(field).asInt();
                }
            }
            return (T) new GoogleFitDataHeartRate(startTime, value, max, min);
        }
        return null;
    }

    public static GoogleFitDataSleep parseDataPointSleep(DataPoint dp, SimpleDateFormat dateFormat) {
        String startTime = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
        String endTime = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));

        boolean isSleep = false;
        for (Field field : dp.getDataType().getFields()) {
            if(dp.getValue(field).toString().equals("72")){
                isSleep = true;
            }

        }
        return isSleep ? new GoogleFitDataSleep(startTime, endTime) : null;
    }

    public interface OnAccessListener {
        void onAccessGoogleFit();
    }
}
