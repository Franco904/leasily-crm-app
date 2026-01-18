package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyTypeEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertyTypeDatabaseDataSource {
    suspend fun insertOrUpdate(propertyType: PropertyTypeEntity): AppResult<Unit, DataError>

    suspend fun getAll(): AppResult<List<PropertyTypeEntity>, DataError>
}
