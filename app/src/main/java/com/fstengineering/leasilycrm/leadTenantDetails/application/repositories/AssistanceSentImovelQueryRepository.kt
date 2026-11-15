package com.fstengineering.leasilycrm.leadTenantDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceSentImovelReadModel
import kotlinx.collections.immutable.ImmutableList

interface AssistanceSentImovelQueryRepository {
    suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<ImmutableList<AssistanceSentImovelReadModel>, ApplicationError>
}
