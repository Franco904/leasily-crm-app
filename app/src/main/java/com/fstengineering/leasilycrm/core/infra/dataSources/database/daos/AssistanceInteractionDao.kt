package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity

@Dao
interface AssistanceInteractionDao {
    @Upsert
    suspend fun insertOrUpdate(assistanceInteraction: AssistanceInteractionEntity)
}
