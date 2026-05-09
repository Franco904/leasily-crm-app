package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity

interface PropertyOwnerFileDatabaseDataSource {
    suspend fun insertOrUpdate(propertyOwnerFile: PropertyOwnerFileEntity)
}
