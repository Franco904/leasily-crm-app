package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceInteractionRepository {
    suspend fun insertOrUpdate(
        interaction: AssistanceInteraction,
    ): AppResult<Unit, DataError>
}
