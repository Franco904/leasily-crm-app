package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicEntity

@Dao
interface PropertyCharacteristicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(propertyCharacteristic: PropertyCharacteristicEntity): Long

    @Query("SELECT * FROM property_characteristics")
    suspend fun getAll(): List<PropertyCharacteristicEntity>
}
