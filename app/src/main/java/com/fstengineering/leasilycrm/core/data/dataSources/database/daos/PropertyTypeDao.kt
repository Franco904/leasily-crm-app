package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyTypeEntity

@Dao
interface PropertyTypeDao {
    @Upsert
    suspend fun insertOrUpdate(propertyType: PropertyTypeEntity)

    @Query("SELECT * FROM property_types")
    suspend fun getAll(): List<PropertyTypeEntity>
}
