package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.Property

@Dao
interface PropertyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(property: Property): Long

    @Delete
    suspend fun delete(property: Property)

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getById(id: Int): Property?

    @Query("SELECT * FROM properties")
    suspend fun getAll(): List<Property>
}
