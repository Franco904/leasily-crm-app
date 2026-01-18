package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class PropertyFileDatabaseDataSourceImpl(
    private val dao: PropertyFileDao,
) : PropertyFileDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyFile: PropertyFileEntity,
    ) =
        dao.insertOrUpdate(propertyFile)
}
