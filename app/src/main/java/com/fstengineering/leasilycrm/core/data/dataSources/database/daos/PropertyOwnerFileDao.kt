package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity

@Dao
interface PropertyOwnerFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(propertyOwnerFile: PropertyOwnerFileEntity): Long

    @Query("SELECT * FROM property_owner_files WHERE property_owner_id = :propertyOwnerId")
    suspend fun getAllByPropertyOwner(propertyOwnerId: Int): List<PropertyOwnerFileEntity>
}
