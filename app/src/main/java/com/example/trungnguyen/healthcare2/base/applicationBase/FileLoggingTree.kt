package com.example.trungnguyen.healthcare2.base.applicationBase

import timber.log.Timber

/**
 * Created by Trung Nguyen on 22-Aug-18.
 */
class FileLoggingTree() : Timber.DebugTree() {

    companion object {
        private val TAG = FileLoggingTree::class.java.getSimpleName()
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
    }
}