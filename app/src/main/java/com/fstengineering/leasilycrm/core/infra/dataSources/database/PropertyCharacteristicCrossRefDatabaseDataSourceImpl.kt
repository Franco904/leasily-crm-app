package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyCharacteristicCrossRefDatabaseDataSource

class PropertyCharacteristicCrossRefDatabaseDataSourceImpl(
    private val dao: PropertyCharacteristicPropertyCrossRefDao,
) : PropertyCharacteristicCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: PropertyCharacteristicCrossRefEntity,
    ) =
        dao.insertOrUpdate(crossRef)
}
