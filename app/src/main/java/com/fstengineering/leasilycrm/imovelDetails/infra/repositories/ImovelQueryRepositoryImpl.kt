package com.fstengineering.leasilycrm.imovelDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDetails.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelDetails.infra.mappers.toRawReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImovelQueryRepositoryImpl(
    private val databaseDataSource: ImovelDatabaseDataSource,
) : ImovelQueryRepository {
    override fun watchById(imovelId: Long): Flow<AppResult<ImovelReadModel, ApplicationError>> {
        return databaseDataSource.watchById(id = imovelId)
            .map { imovel ->
                imovel.fold(
                    onError = { e -> AppResult.Error(error = e) },
                    onSuccess = { AppResult.Success(data = it.toRawReadModel()) },
                )
            }
    }
}
