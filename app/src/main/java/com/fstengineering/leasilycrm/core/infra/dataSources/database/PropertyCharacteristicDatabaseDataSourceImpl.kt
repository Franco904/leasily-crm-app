package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyCharacteristicDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class PropertyCharacteristicDatabaseDataSourceImpl(
    private val dao: PropertyCharacteristicDao,
) : PropertyCharacteristicDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyCharacteristic: PropertyCharacteristicEntity,
    ) =
        dao.insertOrUpdate(propertyCharacteristic)

    override suspend fun getAll(): AppResult<List<PropertyCharacteristicEntity>, DataError> =
        onDatabaseSafe { dao.getAll() }
}
