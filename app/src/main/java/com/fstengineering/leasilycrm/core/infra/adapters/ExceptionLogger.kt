package com.fstengineering.leasilycrm.core.infra.adapters

import android.util.Log
import com.fstengineering.leasilycrm.BuildConfig

object ExceptionLogger {
    private const val TAG = "ExceptionLogger"

    /**
     * Registra uma falha que não é uma exceção capturada, como um dado ausente que o código
     * mascara. A [message] vai com a stacktrace de quem chamou: a exceção é só construída, nunca
     * lançada.
     */
    fun logMasked(message: String) = log(MaskedFailure(message))

    fun log(throwable: Throwable) {
        if (BuildConfig.DEBUG) Log.e(TAG, throwable.message.orEmpty(), throwable)
        // TODO: FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    fun setUserIdentifier(identifier: String) {
        // TODO: FirebaseCrashlytics.getInstance().setUserId(identifier)
    }

    private class MaskedFailure(message: String) : Exception(message)
}
