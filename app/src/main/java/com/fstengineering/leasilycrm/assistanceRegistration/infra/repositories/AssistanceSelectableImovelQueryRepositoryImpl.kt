package com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSelectableImovelReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSelectableImovelQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.mappers.toSelectableImovelReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceSelectableImovelQueryRepositoryImpl(
    private val imovelDatabaseDataSource: ImovelDatabaseDataSource,
) : AssistanceSelectableImovelQueryRepository {
    override suspend fun getAll(
        matching: String?,
    ): AppResult<ImmutableList<AssistanceSelectableImovelReadModel>, ApplicationError> {
        val selectableImoveis = imovelDatabaseDataSource.getAll(matching)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it.map { imovel -> imovel.toSelectableImovelReadModel() } },
            )
            .toPersistentList()

        return AppResult.Success(data = selectableImoveis)
    }
}
