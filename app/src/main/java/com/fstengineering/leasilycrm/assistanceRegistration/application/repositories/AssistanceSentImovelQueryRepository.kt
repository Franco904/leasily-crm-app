package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSentImovelReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList

interface AssistanceSentImovelQueryRepository {
    suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<ImmutableList<AssistanceSentImovelReadModel>>
}
