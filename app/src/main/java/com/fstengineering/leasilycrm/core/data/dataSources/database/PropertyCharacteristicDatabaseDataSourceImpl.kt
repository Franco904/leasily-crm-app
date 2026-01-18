package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class PropertyCharacteristicDatabaseDataSourceImpl(
    private val dao: PropertyCharacteristicDao,
) : PropertyCharacteristicDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyCharacteristic: PropertyCharacteristicEntity,
    ): AppResult<Long, DataError> =
        onDatabase { dao.insertOrUpdate(propertyCharacteristic) }

    override suspend fun getAll(): AppResult<List<PropertyCharacteristicEntity>, DataError> =
        onDatabase { dao.getAll() }
}
