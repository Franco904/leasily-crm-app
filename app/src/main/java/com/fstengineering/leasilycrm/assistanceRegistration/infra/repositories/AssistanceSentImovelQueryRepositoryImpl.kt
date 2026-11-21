package com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSentImovelReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentImovelQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.mappers.toSentImovelReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceSentImovelQueryRepositoryImpl(
    private val imovelDatabaseDataSource: ImovelDatabaseDataSource,
) : AssistanceSentImovelQueryRepository {
    override suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<ImmutableList<AssistanceSentImovelReadModel>> {
        val sentImoveis = imovelDatabaseDataSource.getAllSentInAssistance(assistanceId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it.map { imovel -> imovel.toSentImovelReadModel() } },
            )
            .toPersistentList()

        return AppResult.Success(data = sentImoveis)
    }
}
