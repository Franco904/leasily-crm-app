package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristicProperty
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertyCharacteristicPropertyRepository {
    suspend fun insertOrUpdate(
        characteristicProperty: PropertyCharacteristicProperty,
    ): AppResult<Unit, DataError>
}
