package com.fstengineering.leasilycrm.core.infra.utils

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

inline fun <T> onDatabase(block: () -> T): AppResult<T, ApplicationError> {
    try {
        return AppResult.Success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (e: Exception) {
        ExceptionLogger.log(e)
        return AppResult.Error(ApplicationError.Unknown)
    }
}

fun <T> Flow<T>.asDatabaseFlowResult(): Flow<AppResult<T, ApplicationError>> {
    return distinctUntilChanged()
        .map<T, AppResult<T, ApplicationError>> { value -> AppResult.Success(value) }
        .catch { cause ->
            if (cause !is Exception || cause is CancellationException) throw cause
            ExceptionLogger.log(cause)

            emit(AppResult.Error(ApplicationError.Unknown))
        }
}
