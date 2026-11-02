package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentImovelCrossRefEntity

@Dao
interface AssistanceSentImovelCrossRefDao {
    @Upsert
    suspend fun insertOrUpdate(crossRef: AssistanceSentImovelCrossRefEntity)
}
