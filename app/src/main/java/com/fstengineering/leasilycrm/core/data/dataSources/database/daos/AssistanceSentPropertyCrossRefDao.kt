package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity

@Dao
interface AssistanceSentPropertyCrossRefDao {
    @Upsert
    suspend fun insertOrUpdate(crossRef: AssistanceSentPropertyCrossRefEntity): Long

    @Delete
    suspend fun delete(crossRef: AssistanceSentPropertyCrossRefEntity)
}
