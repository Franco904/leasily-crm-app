package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFile

@Dao
interface PropertyFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(propertyFile: PropertyFile): Long

    @Query("SELECT * FROM property_files WHERE property_id = :propertyId")
    suspend fun getAllByProperty(propertyId: Int): List<PropertyFile>
}
