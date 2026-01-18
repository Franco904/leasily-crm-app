package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity

@Dao
interface StayTransferDao {
    @Upsert
    suspend fun insertOrUpdate(stayTransfer: StayTransferEntity)
}
