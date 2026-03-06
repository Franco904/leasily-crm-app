package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceQueryModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceRawReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.core.application.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class ReadAssistanceUseCase(
    private val tenantRepository: TenantRepository,
    private val assistanceQueryRepository: AssistanceQueryRepository,
    private val assistanceSentPropertyQueryRepository: AssistanceSentPropertyQueryRepository,
) : QueryUseCase<AssistanceQueryModel, AssistanceReadModel> {
    override suspend fun invoke(
        query: AssistanceQueryModel,
    ): AppResult<AssistanceReadModel, ApplicationError> {
        val tenantName = tenantRepository.getNameById(tenantId = query.leadTenantId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { tenantName -> tenantName },
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
            leadTenantName = tenantName,
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
