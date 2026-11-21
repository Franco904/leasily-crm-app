package com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.mappers.toReadModel

class ImovelQueryRepositoryImpl(
    private val imovelDatabaseDataSource: ImovelDatabaseDataSource,
) : ImovelQueryRepository {
    override suspend fun getById(imovelId: Long): AppResult<ImovelReadModel> {
        return imovelDatabaseDataSource.getById(id = imovelId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) },
            )
    }
}
