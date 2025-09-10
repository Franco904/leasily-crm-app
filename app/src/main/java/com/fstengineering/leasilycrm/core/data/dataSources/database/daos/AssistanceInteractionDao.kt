package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity

@Dao
interface AssistanceInteractionDao {
    @Upsert
    suspend fun insertOrUpdate(assistanceInteraction: AssistanceInteractionEntity): Long

    @Query("SELECT * FROM assistance_interactions WHERE assistance_id = :assistanceId")
    suspend fun getAllByAssistance(assistanceId: Long): List<AssistanceInteractionEntity>
}
