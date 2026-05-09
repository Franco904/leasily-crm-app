package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyFileDatabaseDataSource

class PropertyFileDatabaseDataSourceImpl(
    private val dao: PropertyFileDao,
) : PropertyFileDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyFile: PropertyFileEntity,
    ) =
        dao.insertOrUpdate(propertyFile)
}
