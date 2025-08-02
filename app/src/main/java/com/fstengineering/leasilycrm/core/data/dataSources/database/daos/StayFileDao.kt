package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFileEntity

@Dao
interface StayFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stayFile: StayFileEntity): Long

    @Query("SELECT * FROM stay_files WHERE stay_id = :stayId")
    suspend fun getAllByStay(stayId: Int): List<StayFileEntity>
}
