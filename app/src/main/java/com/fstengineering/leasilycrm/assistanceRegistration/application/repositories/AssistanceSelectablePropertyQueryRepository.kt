package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSelectablePropertyReadModel
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList

interface AssistanceSelectablePropertyQueryRepository {
    suspend fun getAll(
        matching: String? = null,
    ): AppResult<ImmutableList<AssistanceSelectablePropertyReadModel>, ApplicationError>
}
