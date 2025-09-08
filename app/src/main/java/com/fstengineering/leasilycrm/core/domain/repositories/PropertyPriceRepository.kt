package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface PropertyPriceRepository {
    suspend fun insertOrUpdate(price: PropertyPrice): AppResult<Unit, DataSourceError>
}
