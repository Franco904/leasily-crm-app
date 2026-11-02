package com.fstengineering.leasilycrm.imovelRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelRawReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelRegistration.infra.mappers.toRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class ImovelQueryRepositoryImpl(
    private val imovelDatabaseDataSource: ImovelDatabaseDataSource,
) : ImovelQueryRepository {
    override suspend fun getById(imovelId: Long): AppResult<ImovelRawReadModel, ApplicationError> {
        return imovelDatabaseDataSource.getById(id = imovelId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toRawReadModel()) },
            )
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, ApplicationError> {
        val neighborHoodNames =
            imovelDatabaseDataSource.getAllNeighborhoodNames()
                .fold(
                    onError = { return AppResult.Error(error = it) },
                    onSuccess = { it },
                )
                .toPersistentList()

        return AppResult.Success(data = neighborHoodNames)
    }
}
