package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceRawReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList

interface AssistanceQueryRepository {
    suspend fun getById(assistanceId: Long): AppResult<AssistanceRawReadModel>

    suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>>
}