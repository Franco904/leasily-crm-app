package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity

interface PropertyCharacteristicPropertyCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: PropertyCharacteristicPropertyCrossRefEntity,
    )
}
