package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLogger
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

inline fun <T> onDatabaseSafe(exceptionLogger: ExceptionLogger, block: () -> T): AppResult<T, InfraError> {
    try {
        return AppResult.Success(block())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        exceptionLogger.log(e)

        return AppResult.Error(InfraError.Unknown)
    }
}

fun <T> Flow<T>.asDatabaseFlowResult(exceptionLogger: ExceptionLogger): Flow<AppResult<T, InfraError>> {
    return distinctUntilChanged()
        .map<T, AppResult<T, InfraError>> { value -> AppResult.Success(value) }
        .catch { e ->
            if (e is CancellationException) throw e
            exceptionLogger.log(e)

            emit(AppResult.Error(InfraError.Unknown))
        }
}
