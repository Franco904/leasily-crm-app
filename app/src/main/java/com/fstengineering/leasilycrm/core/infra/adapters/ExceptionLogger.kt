package com.fstengineering.leasilycrm.core.infra.adapters

import android.util.Log
import com.fstengineering.leasilycrm.BuildConfig

object ExceptionLogger {
    private const val TAG = "ExceptionLogger"

    fun log(throwable: Throwable) {
        if (BuildConfig.DEBUG) Log.e(TAG, throwable.message.orEmpty(), throwable)
        // TODO: FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    fun setUserIdentifier(identifier: String) {
        // TODO: FirebaseCrashlytics.getInstance().setUserId(identifier)
    }
}
