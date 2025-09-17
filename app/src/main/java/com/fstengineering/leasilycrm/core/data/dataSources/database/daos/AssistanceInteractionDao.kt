package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity

@Dao
interface AssistanceInteractionDao {
    @Upsert
    suspend fun insertOrUpdate(assistanceInteraction: AssistanceInteractionEntity): Long
}
