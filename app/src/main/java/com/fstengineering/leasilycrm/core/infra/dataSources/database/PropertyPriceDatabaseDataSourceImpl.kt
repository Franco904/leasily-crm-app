package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyPriceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyPriceDatabaseDataSource

class PropertyPriceDatabaseDataSourceImpl(
    private val dao: PropertyPriceDao,
) : PropertyPriceDatabaseDataSource {
    override suspend fun insertOrUpdate(
        propertyPrice: PropertyPriceEntity,
    ) =
        dao.insertOrUpdate(propertyPrice)
}
