package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertyPriceDatabaseDataSource {
    suspend fun insertOrUpdate(propertyPrice: PropertyPriceEntity)
}
