package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFileEntity

interface PropertyFileDatabaseDataSource {
    suspend fun insertOrUpdate(propertyFile: PropertyFileEntity)
}
