package com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class ImovelQueryRepositoryImpl(
    private val databaseDataSource: ImovelDatabaseDataSource,
) : ImovelQueryRepository {
    override suspend fun getById(imovelId: Long): AppResult<ImovelReadModel, ApplicationError> {
        return databaseDataSource.getById(id = imovelId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) },
            )
    }

    override suspend fun getAll(
        matching: String?,
    ): AppResult<ImmutableList<ImovelReadModel>, ApplicationError> {
        val properties = databaseDataSource.getAll(matching)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .map { property -> property.toReadModel() }.toPersistentList()

        return AppResult.Success(data = properties)
    }
}
