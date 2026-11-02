package com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceSentImovelReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceSentImovelQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.infra.mappers.toSentImovelReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceSentImovelQueryRepositoryImpl(
    private val imovelDatabaseDataSource: ImovelDatabaseDataSource,
) : AssistanceSentImovelQueryRepository {
    override suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<ImmutableList<AssistanceSentImovelReadModel>, ApplicationError> {
        val sentImoveis = imovelDatabaseDataSource.getAllSentInAssistance(assistanceId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it.map { imovel -> imovel.toSentImovelReadModel() } },
            )
            .toPersistentList()

        return AppResult.Success(data = sentImoveis)
    }
}
