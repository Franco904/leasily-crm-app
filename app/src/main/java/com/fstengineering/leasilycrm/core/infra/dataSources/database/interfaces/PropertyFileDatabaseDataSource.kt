package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity

interface PropertyFileDatabaseDataSource {
    suspend fun insertOrUpdate(propertyFile: PropertyFileEntity)
}
