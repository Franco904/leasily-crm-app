package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSelectableImovelReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import kotlinx.collections.immutable.ImmutableList

interface AssistanceSelectableImovelQueryRepository {
    suspend fun getAll(
        matching: String? = null,
    ): AppResult<ImmutableList<AssistanceSelectableImovelReadModel>, ApplicationError>
}
