package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyOwnerFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerFileDatabaseDataSource

class PropertyOwnerFileDatabaseDataSourceImpl(
    private val dao: PropertyOwnerFileDao,
) : PropertyOwnerFileDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyOwnerFile: PropertyOwnerFileEntity,
    ) =
        dao.insertOrUpdate(propertyOwnerFile)
}
