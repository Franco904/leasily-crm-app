package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity

@Dao
interface PropertyPriceDao {
    @Upsert
    suspend fun insertOrUpdate(propertyPrice: PropertyPriceEntity)
}
