package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertySeaProximityTypeDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertySeaProximityTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class PropertySeaProximityTypeDatabaseDataSourceImpl(
    private val dao: PropertySeaProximityTypeDao,
) : PropertySeaProximityTypeDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertySeaProximityType: PropertySeaProximityTypeEntity,
    ) =
        dao.insertOrUpdate(propertySeaProximityType)

    override suspend fun getAll(): AppResult<List<PropertySeaProximityTypeEntity>, DataError> =
        onDatabaseSafe { dao.getAll() }
}
