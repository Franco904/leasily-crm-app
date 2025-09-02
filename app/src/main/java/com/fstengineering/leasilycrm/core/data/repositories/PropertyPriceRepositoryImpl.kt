package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyPriceDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyPriceRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

class PropertyPriceRepositoryImpl(
    private val dao: PropertyPriceDao,
) : PropertyPriceRepository {
    override suspend fun insertOrUpdate(price: PropertyPrice): AppResult<Unit, DataSourceError> {
        val priceEntity = PropertyPriceEntity.fromDomain(domain = price)
        dao.insertOrUpdate(priceEntity)

        return AppResult.Success(data = Unit)
    }
}
