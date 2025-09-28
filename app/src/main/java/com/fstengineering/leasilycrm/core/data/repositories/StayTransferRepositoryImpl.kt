package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.repositories.StayTransferRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

class StayTransferRepositoryImpl(
    private val dao: StayTransferDao,
) : StayTransferRepository {
    override suspend fun insertOrUpdate(transfer: StayTransfer): AppResult<Unit, DataSourceError> {
        val transferEntity = StayTransferEntity.fromDomain(domain = transfer)
        dao.insertOrUpdate(transferEntity)

        return AppResult.Success(data = Unit)
    }
}
