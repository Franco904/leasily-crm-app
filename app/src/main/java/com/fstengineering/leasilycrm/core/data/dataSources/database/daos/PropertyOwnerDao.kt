package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity

@Dao
interface PropertyOwnerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): Long

    @Query("SELECT * FROM property_owners")
    suspend fun getAll(): List<PropertyOwnerEntity>
}
