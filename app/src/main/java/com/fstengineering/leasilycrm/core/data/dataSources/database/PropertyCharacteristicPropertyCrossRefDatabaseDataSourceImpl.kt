package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyCharacteristicPropertyCrossRefDatabaseDataSource

class PropertyCharacteristicPropertyCrossRefDatabaseDataSourceImpl(
    private val dao: PropertyCharacteristicPropertyCrossRefDao,
) : PropertyCharacteristicPropertyCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: PropertyCharacteristicPropertyCrossRefEntity,
    ) =
        dao.insertOrUpdate(crossRef)
}
