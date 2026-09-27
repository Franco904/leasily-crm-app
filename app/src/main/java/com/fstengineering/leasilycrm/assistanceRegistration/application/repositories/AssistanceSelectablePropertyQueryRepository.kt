package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSelectablePropertyReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import kotlinx.collections.immutable.ImmutableList

interface AssistanceSelectablePropertyQueryRepository {
    suspend fun getAll(
        matching: String? = null,
    ): AppResult<ImmutableList<AssistanceSelectablePropertyReadModel>, ApplicationError>
}
