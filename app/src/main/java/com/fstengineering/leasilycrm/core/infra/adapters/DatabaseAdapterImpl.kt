package com.fstengineering.leasilycrm.core.infra.adapters

import androidx.room.withTransaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

class DatabaseAdapterImpl(
    private val db: AppDatabase,
) : DatabaseAdapter {
    override suspend fun transaction(
        transactionBlock: suspend () -> Unit,
    ): AppResult<Unit> {
        return onDatabase { db.withTransaction { transactionBlock() } }
    }
}
