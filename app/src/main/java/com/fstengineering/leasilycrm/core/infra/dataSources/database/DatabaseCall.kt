package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlin.coroutines.cancellation.CancellationException

inline fun <T> onDatabaseSafe(block: () -> T): AppResult<T, DataError> {
    try {
        return AppResult.Success(block())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        if (BuildConfig.DEBUG) e.printStackTrace()

        return AppResult.Error(DataError.UnknownError)
    }
}
