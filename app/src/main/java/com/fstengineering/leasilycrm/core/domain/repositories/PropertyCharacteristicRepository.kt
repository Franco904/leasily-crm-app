package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList

interface PropertyCharacteristicRepository {
    suspend fun getAll(): AppResult<ImmutableList<PropertyCharacteristic>, DataSourceError>
}
