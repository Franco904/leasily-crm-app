package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertyCharacteristicDatabaseDataSource {
    suspend fun insertOrUpdate(propertyCharacteristic: PropertyCharacteristicEntity)

    suspend fun getAll(): AppResult<List<PropertyCharacteristicEntity>, DataError>
}
