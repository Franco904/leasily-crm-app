package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicEntity

@Dao
interface PropertyCharacteristicDao {
    @Upsert
    suspend fun insertOrUpdate(propertyCharacteristic: PropertyCharacteristicEntity)

    @Query("SELECT * FROM property_characteristics")
    suspend fun getAll(): List<PropertyCharacteristicEntity>
}
