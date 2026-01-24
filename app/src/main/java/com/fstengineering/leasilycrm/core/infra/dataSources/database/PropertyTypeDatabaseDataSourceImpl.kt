package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyTypeDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyTypeEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class PropertyTypeDatabaseDataSourceImpl(
    private val dao: PropertyTypeDao,
) : PropertyTypeDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyType: PropertyTypeEntity,
    ) =
        dao.insertOrUpdate(propertyType)

    override suspend fun getAll(): AppResult<List<PropertyTypeEntity>, DataError> =
        onDatabaseSafe { dao.getAll() }
}
