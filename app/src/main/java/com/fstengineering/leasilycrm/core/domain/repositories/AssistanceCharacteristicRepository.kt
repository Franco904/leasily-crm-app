package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList

interface AssistanceCharacteristicRepository {
    suspend fun getAll(): AppResult<ImmutableList<AssistanceCharacteristic>, DataError>
}
