package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.StayTransferDatabaseDataSource

class StayTransferDatabaseDataSourceImpl(
    private val dao: StayTransferDao,
) : StayTransferDatabaseDataSource {
    override suspend fun insertOrUpdate(
        stayTransfer: StayTransferEntity,
    ) =
        dao.insertOrUpdate(stayTransfer)
}
