package com.fstengineering.leasilycrm.core.data.adapters

import androidx.room.withTransaction
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.data.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.data.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlin.coroutines.cancellation.CancellationException

class DatabaseAdapterImpl(private val db: AppDatabase) : DatabaseAdapter {
    override suspend fun withTransaction(
        transactionBlock: suspend () -> Unit,
    ): AppResult<Unit, DataError> {
        try {
            db.withTransaction { transactionBlock() }

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(DataError.UnknownError)
        }
    }
}
