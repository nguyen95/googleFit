package com.example.trungnguyen.healthcare2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import timber.log.Timber.e
import java.text.DateFormat.getTimeInstance
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        e("hihiiiiiiiiiiiiiiiiiiiii")
        checkSignIn()
    }

    fun checkSignIn() {
        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build()

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions)
        } else {
            accessGoogleFit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            accessGoogleFit()
        }
    }

    fun accessGoogleFit() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val cal = Calendar.getInstance()
        with(cal) {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = cal.timeInMillis

        val readRequest = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build()

        account?.let {
            Fitness.getHistoryClient(this, it)
//                    .readData(readRequest)
                    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener {
                        dumpDataSet(it)
//                        if (it.buckets.size > 0) {
//                            for (bucket in it.buckets) {
//                                for (dataSet in bucket.dataSets) dumpDataSet(dataSet)
//                            }
//                        } else if (it.dataSets.size > 0) {
//                            for (dataSet in it.dataSets) dumpDataSet(dataSet)
//                        }
                    }
                    .addOnFailureListener {
                        e("onFailure: ${it}")
                    }
        }
    }

    fun dumpDataSet(dataSet: DataSet) {
        e("Data returned for Data type: ${dataSet.dataType.getName()}")
        val dateFormat = getTimeInstance()

        for (dp in dataSet.dataPoints) {
            e("Data point:")
            e("\tType: ${dp.dataType.getName()}")
            e("\tStart: ${dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))}")
            e("\tEnd: ${dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))}")
            for (field in dp.dataType.fields) {
                e("\tField: ${field.name}  Value: ${dp.getValue(field)}")
            }
        }
    }
}
