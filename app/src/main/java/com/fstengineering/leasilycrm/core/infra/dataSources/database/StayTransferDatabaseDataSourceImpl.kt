package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayTransferDatabaseDataSource

class StayTransferDatabaseDataSourceImpl(
    private val dao: StayTransferDao,
) : StayTransferDatabaseDataSource {
    override suspend fun insertOrUpdate(
        stayTransfer: StayTransferEntity,
    ) =
        dao.insertOrUpdate(stayTransfer)
}
