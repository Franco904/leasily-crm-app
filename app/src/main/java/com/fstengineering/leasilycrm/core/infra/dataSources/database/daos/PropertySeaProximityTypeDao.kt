package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertySeaProximityTypeEntity

@Dao
interface PropertySeaProximityTypeDao {
    @Upsert
    suspend fun insertOrUpdate(propertySeaProximityType: PropertySeaProximityTypeEntity)

    @Query("SELECT * FROM property_sea_proximity_types")
    suspend fun getAll(): List<PropertySeaProximityTypeEntity>
}
