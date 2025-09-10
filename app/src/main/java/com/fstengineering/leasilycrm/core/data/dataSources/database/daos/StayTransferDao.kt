package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity

@Dao
interface StayTransferDao {
    @Upsert
    suspend fun insertOrUpdate(stayTransfer: StayTransferEntity): Long

    @Query("SELECT * FROM stay_transfers WHERE stay_id = :stayId")
    suspend fun getAllByStay(stayId: Long): List<StayTransferEntity>
}
