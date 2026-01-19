package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyOwnerFileDatabaseDataSource

class PropertyOwnerFileDatabaseDataSourceImpl(
    private val dao: PropertyOwnerFileDao,
) : PropertyOwnerFileDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyOwnerFile: PropertyOwnerFileEntity,
    ) =
        dao.insertOrUpdate(propertyOwnerFile)
}
