package com.example.trungnguyen.healthcare2.base.applicationBase

import android.app.Application
import timber.log.Timber

/**
 * Created by Trung Nguyen on 22-Aug-18.
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(FileLoggingTree())
    }
}
