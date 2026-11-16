package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceRawReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSentImovelReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.queries.AssistanceArgs
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentImovelQueryRepository
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ClienteSharedReadDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class ReadAssistanceUseCase(
    private val clienteSharedReadDataSource: ClienteSharedReadDataSource,
    private val assistanceQueryRepository: AssistanceQueryRepository,
    private val assistanceSentImovelQueryRepository: AssistanceSentImovelQueryRepository,
) {
    suspend operator fun invoke(
        args: AssistanceArgs,
    ): AppResult<AssistanceReadModel, ApplicationError> {
        val clienteName = clienteSharedReadDataSource.getName(clienteId = args.clienteId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { clienteName -> clienteName },
            )

        val assistanceRaw = loadAssistance(args.assistanceId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedAssistance -> storedAssistance },
            )

        val sentImoveis = loadSentImoveis(args.assistanceId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedSentImoveis -> storedSentImoveis },
            )

        val updatedReadModel = AssistanceReadModel.create(
            clienteId = args.clienteId,
            clienteName = clienteName,
            assistanceRaw = assistanceRaw,
            sentImoveis = sentImoveis,
        )

        return AppResult.Success(data = updatedReadModel)
    }

    private suspend fun loadAssistance(
        assistanceId: Long?,
    ): AppResult<AssistanceRawReadModel?, ApplicationError> {
        if (assistanceId == null) {
            return AppResult.Success(null)
        }

        return assistanceQueryRepository.getById(assistanceId)
    }

    private suspend fun loadSentImoveis(
        assistanceId: Long?,
    ): AppResult<ImmutableList<AssistanceSentImovelReadModel>, ApplicationError> {
        if (assistanceId == null) {
            return AppResult.Success(persistentListOf())
        }

        return assistanceSentImovelQueryRepository.getAllSentInAssistance(assistanceId)
    }
}
