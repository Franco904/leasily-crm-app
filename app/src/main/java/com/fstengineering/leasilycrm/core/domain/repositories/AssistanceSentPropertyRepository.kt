package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.AssistanceSentProperty
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceSentPropertyRepository {
    suspend fun insertOrUpdate(
        sentProperty: AssistanceSentProperty,
    ): AppResult<Unit, DataError>
}
