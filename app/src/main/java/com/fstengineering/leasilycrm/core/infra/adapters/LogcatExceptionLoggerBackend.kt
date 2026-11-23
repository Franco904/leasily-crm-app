package com.fstengineering.leasilycrm.core.infra.adapters

import android.util.Log
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLoggerBackend

class LogcatExceptionLoggerBackend : ExceptionLoggerBackend {
    override fun log(throwable: Throwable) {
        if (BuildConfig.DEBUG) Log.e(TAG, throwable.message.orEmpty(), throwable)
        // TODO: FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    private companion object {
        const val TAG = "ExceptionLogger"
    }
}
