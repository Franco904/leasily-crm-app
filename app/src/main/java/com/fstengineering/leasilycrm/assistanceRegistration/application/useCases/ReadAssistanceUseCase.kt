package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceArgs
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceRawReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class ReadAssistanceUseCase(
    private val leadTenantQueryRepository: LeadTenantQueryRepository,
    private val assistanceQueryRepository: AssistanceQueryRepository,
    private val assistanceSentPropertyQueryRepository: AssistanceSentPropertyQueryRepository,
) : QueryUseCase<AssistanceArgs, AssistanceReadModel> {
    override suspend fun invoke(
        query: AssistanceArgs,
    ): AppResult<AssistanceReadModel, ApplicationError> {
        val leadTenantName = leadTenantQueryRepository.getNameById(tenantId = query.leadTenantId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { leadTenantName -> leadTenantName },
            )

        val assistanceRaw = loadAssistance(query.assistanceId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedAssistance -> storedAssistance },
            )

        val sentProperties = loadSentProperties(query.assistanceId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedSentProperties -> storedSentProperties },
            )

        val updatedReadModel = AssistanceReadModel.create(
            leadTenantId = query.leadTenantId,
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
