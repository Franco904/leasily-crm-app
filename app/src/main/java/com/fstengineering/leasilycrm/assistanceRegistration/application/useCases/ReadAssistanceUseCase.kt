package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceQueryModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceReadModel
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.application.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.persistentListOf

class ReadAssistanceUseCase(
    private val tenantRepository: TenantRepository,
    private val assistanceRepository: AssistanceRepository,
    private val propertyRepository: PropertyRepository,
) : QueryUseCase<AssistanceQueryModel, AssistanceReadModel> {
    override suspend fun invoke(
        query: AssistanceQueryModel,
    ): AppResult<AssistanceReadModel, ApplicationError> {
        val tenantName = tenantRepository.getNameById(tenantId = query.leadTenantId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { tenantName -> tenantName },
            )

        val assistance = query.assistanceId?.let {
            assistanceRepository.getById(query.assistanceId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedAssistance -> storedAssistance },
                )
        }

        val sentProperties = query.assistanceId?.let {
            propertyRepository.getAllSentInAssistance(query.assistanceId)
                .fold(
                    onError = { e -> return AppResult.Error(error = e) },
                    onSuccess = { storedSentProperties -> storedSentProperties },
                )
        } ?: persistentListOf()

        val readModel = AssistanceReadModel(
            assistance = assistance,
            sentProperties = sentProperties,
            leadTenantId = query.leadTenantId,
            leadTenantName = tenantName,
        )

        return AppResult.Success(data = readModel)
    }
}
