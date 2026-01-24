package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity

interface PropertyPriceDatabaseDataSource {
    suspend fun insertOrUpdate(propertyPrice: PropertyPriceEntity)
}
