package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import kotlinx.collections.immutable.ImmutableList

interface AssistanceSentPropertyQueryRepository {
    suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<ImmutableList<AssistanceSentPropertyReadModel>, ApplicationError>
}
