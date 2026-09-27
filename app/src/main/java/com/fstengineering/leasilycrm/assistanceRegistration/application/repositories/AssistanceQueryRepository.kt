package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceRawReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import kotlinx.collections.immutable.ImmutableList

interface AssistanceQueryRepository {
    suspend fun getById(assistanceId: Long): AppResult<AssistanceRawReadModel, ApplicationError>

    suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, ApplicationError>
}