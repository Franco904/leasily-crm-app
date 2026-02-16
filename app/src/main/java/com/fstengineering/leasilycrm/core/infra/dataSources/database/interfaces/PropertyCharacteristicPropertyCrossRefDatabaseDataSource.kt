package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicCrossRefEntity

interface PropertyCharacteristicPropertyCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: PropertyCharacteristicCrossRefEntity,
    )
}
