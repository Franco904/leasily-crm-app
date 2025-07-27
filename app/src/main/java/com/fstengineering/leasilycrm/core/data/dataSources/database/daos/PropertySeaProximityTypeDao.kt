package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityType

@Dao
interface PropertySeaProximityTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(propertySeaProximityType: PropertySeaProximityType): Long

    @Query("SELECT * FROM property_sea_proximity_types")
    suspend fun getAll(): List<PropertySeaProximityType>
}
