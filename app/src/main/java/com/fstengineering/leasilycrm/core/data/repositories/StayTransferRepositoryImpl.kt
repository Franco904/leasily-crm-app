package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.StayTransferDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.repositories.StayTransferRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class StayTransferRepositoryImpl(
    private val databaseDataSource: StayTransferDatabaseDataSource,
) : StayTransferRepository {
    override suspend fun insertOrUpdate(transfer: StayTransfer): AppResult<Unit, DataError> {
        val transferEntity = StayTransferEntity.fromDomain(domain = transfer)

        databaseDataSource.insertOrUpdate(transferEntity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
