package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwner

@Dao
interface PropertyOwnerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(propertyOwner: PropertyOwner): Long

    @Query("SELECT * FROM property_owners")
    suspend fun getAll(): List<PropertyOwner>
}
