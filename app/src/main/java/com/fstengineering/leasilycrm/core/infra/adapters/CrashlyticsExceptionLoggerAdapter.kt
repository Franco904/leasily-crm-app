package com.fstengineering.leasilycrm.core.infra.adapters

import android.util.Log
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLogger

class CrashlyticsExceptionLoggerAdapter : ExceptionLogger {
    override fun log(throwable: Throwable) {
        if (BuildConfig.DEBUG) Log.e(TAG, throwable.message.orEmpty(), throwable)
        // TODO: FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    override fun setUserIdentifier(identifier: String) {
        // TODO: FirebaseCrashlytics.getInstance().setUserId(identifier)
    }

    private companion object {
        const val TAG = "ExceptionLogger"
    }
}
