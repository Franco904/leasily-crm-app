package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList

interface PropertyCharacteristicRepository {
    suspend fun getAll(): AppResult<ImmutableList<PropertyCharacteristic>, DataError>
}
