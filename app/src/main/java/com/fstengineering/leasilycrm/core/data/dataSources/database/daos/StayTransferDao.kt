package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransfer

@Dao
interface StayTransferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stayTransfer: StayTransfer): Long

    @Query("SELECT * FROM stay_transfers WHERE stay_id = :stayId")
    suspend fun getAllByStay(stayId: Int): List<StayTransfer>
}
