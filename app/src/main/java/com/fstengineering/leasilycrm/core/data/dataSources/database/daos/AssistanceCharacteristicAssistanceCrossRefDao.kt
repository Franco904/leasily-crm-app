package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity

@Dao
interface AssistanceCharacteristicAssistanceCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(crossRef: AssistanceCharacteristicAssistanceCrossRefEntity)

    @Delete
    suspend fun delete(crossRef: AssistanceCharacteristicAssistanceCrossRefEntity)
}
