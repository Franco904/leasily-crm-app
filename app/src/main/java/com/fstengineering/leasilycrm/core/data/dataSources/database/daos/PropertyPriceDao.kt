package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPriceEntity

@Dao
interface PropertyPriceDao {
    @Upsert
    suspend fun insertOrUpdate(propertyPrice: PropertyPriceEntity)
}
