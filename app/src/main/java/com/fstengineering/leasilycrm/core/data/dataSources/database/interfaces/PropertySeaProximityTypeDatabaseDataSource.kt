package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertySeaProximityTypeDatabaseDataSource {
    suspend fun insertOrUpdate(
        propertySeaProximityType: PropertySeaProximityTypeEntity,
    ): AppResult<Unit, DataError>

    suspend fun getAll(): AppResult<List<PropertySeaProximityTypeEntity>, DataError>
}
