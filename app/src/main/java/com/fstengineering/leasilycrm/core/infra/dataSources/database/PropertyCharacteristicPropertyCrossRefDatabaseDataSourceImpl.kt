package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyCharacteristicPropertyCrossRefDatabaseDataSource

class PropertyCharacteristicPropertyCrossRefDatabaseDataSourceImpl(
    private val dao: PropertyCharacteristicPropertyCrossRefDao,
) : PropertyCharacteristicPropertyCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: PropertyCharacteristicCrossRefEntity,
    ) =
        dao.insertOrUpdate(crossRef)
}
