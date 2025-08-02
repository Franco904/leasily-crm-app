package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity

@Dao
interface AssistanceInteractionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(assistanceInteraction: AssistanceInteractionEntity): Long

    @Query("SELECT * FROM assistance_interactions WHERE assistance_id = :assistanceId")
    suspend fun getAllByAssistance(assistanceId: Int): List<AssistanceInteractionEntity>
}
