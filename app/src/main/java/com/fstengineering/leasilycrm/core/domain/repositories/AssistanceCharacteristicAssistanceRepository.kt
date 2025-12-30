package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristicAssistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceCharacteristicAssistanceRepository {
    suspend fun insertOrUpdate(
        characteristicAssistance: AssistanceCharacteristicAssistance,
    ): AppResult<Unit, DataError>
}
