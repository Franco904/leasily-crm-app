package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.StayTransferDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class StayTransferDatabaseDataSourceImpl(
    private val dao: StayTransferDao,
) : StayTransferDatabaseDataSource {
    override suspend fun insertOrUpdate(
        stayTransfer: StayTransferEntity,
    ): AppResult<Unit, DataError> =
        onDatabase { dao.insertOrUpdate(stayTransfer) }
}
