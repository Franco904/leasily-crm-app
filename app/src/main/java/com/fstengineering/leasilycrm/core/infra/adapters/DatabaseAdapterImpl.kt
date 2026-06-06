package com.fstengineering.leasilycrm.core.infra.adapters

import androidx.room.withTransaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLogger
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlin.coroutines.cancellation.CancellationException

class DatabaseAdapterImpl(
    private val db: AppDatabase,
    private val exceptionLogger: ExceptionLogger,
) : DatabaseAdapter {
    override suspend fun withTransaction(
        transactionBlock: suspend () -> Unit,
    ): AppResult<Unit, InfraError> {
        try {
            db.withTransaction { transactionBlock() }

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            exceptionLogger.log(e)

            return AppResult.Error(InfraError.Unknown)
        }
    }
}
