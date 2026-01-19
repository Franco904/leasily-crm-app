package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPriceEntity

interface PropertyPriceDatabaseDataSource {
    suspend fun insertOrUpdate(propertyPrice: PropertyPriceEntity)
}
