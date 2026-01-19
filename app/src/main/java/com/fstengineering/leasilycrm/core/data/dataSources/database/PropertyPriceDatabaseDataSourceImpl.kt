package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyPriceDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyPriceDatabaseDataSource

class PropertyPriceDatabaseDataSourceImpl(
    private val dao: PropertyPriceDao,
) : PropertyPriceDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyPrice: PropertyPriceEntity,
    ) =
        dao.insertOrUpdate(propertyPrice)
}
