package com.fstengineering.leasilycrm.core.infra.adapters

import androidx.room.withTransaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import kotlin.coroutines.cancellation.CancellationException

class DatabaseAdapterImpl(
    private val db: AppDatabase,
) : DatabaseAdapter {
    override suspend fun transaction(
        transactionBlock: suspend () -> Unit,
    ): AppResult<Unit, ApplicationError> {
        try {
            db.withTransaction { transactionBlock() }

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ExceptionLogger.log(e)

            return AppResult.Error(ApplicationError.Unknown)
        }
    }
}
