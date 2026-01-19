package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity

interface PropertyOwnerFileDatabaseDataSource {
    suspend fun insertOrUpdate(propertyOwnerFile: PropertyOwnerFileEntity)
}
