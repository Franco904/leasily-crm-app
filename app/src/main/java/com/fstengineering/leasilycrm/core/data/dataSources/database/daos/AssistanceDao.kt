package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity

@Dao
interface AssistanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(assistance: AssistanceEntity): Long

    @Delete
    suspend fun delete(assistance: AssistanceEntity)

    @Query("SELECT * FROM assistances WHERE id = :id")
    suspend fun getById(id: Int): AssistanceEntity?

    @Query("SELECT * FROM assistances")
    suspend fun getAll(): List<AssistanceEntity>
}
