package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertySeaProximityTypeDatabaseDataSource {
    suspend fun insertOrUpdate(propertySeaProximityType: PropertySeaProximityTypeEntity)

    suspend fun getAll(): AppResult<List<PropertySeaProximityTypeEntity>, DataError>
}
