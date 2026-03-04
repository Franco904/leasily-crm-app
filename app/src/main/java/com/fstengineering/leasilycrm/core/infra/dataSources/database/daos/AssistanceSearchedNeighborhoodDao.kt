package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity

@Dao
interface AssistanceSearchedNeighborhoodDao {
    @Upsert
    suspend fun insertOrUpdate(neighborhood: AssistancePreferredNeighborhoodEntity)
}
