package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceRawReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.queries.AssistanceArgs
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class ReadAssistanceUseCase(
    private val leadTenantQueryRepository: LeadTenantQueryRepository,
    private val assistanceQueryRepository: AssistanceQueryRepository,
    private val assistanceSentPropertyQueryRepository: AssistanceSentPropertyQueryRepository,
) {
    suspend operator fun invoke(
        args: AssistanceArgs,
    ): AppResult<AssistanceReadModel, ApplicationError> {
        val leadTenantName = leadTenantQueryRepository.getNameById(tenantId = args.leadTenantId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { leadTenantName -> leadTenantName },
            )

        val assistanceRaw = loadAssistance(args.assistanceId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedAssistance -> storedAssistance },
            )

        val sentProperties = loadSentProperties(args.assistanceId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedSentProperties -> storedSentProperties },
            )

        val updatedReadModel = AssistanceReadModel.create(
            leadTenantId = args.leadTenantId,
            leadTenantName = leadTenantName,
            assistanceRaw = assistanceRaw,
            sentProperties = sentProperties,
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

    private suspend fun loadSentProperties(
        assistanceId: Long?,
    ): AppResult<ImmutableList<AssistanceSentPropertyReadModel>, ApplicationError> {
        if (assistanceId == null) {
            return AppResult.Success(persistentListOf())
        }

        return assistanceSentPropertyQueryRepository.getAllSentInAssistance(assistanceId)
    }
}
