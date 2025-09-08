package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPriceEntity

@Dao
interface PropertyPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(propertyPrice: PropertyPriceEntity): Long
}
