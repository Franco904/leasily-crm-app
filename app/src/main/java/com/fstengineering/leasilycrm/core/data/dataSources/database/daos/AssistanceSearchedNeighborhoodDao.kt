package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity

@Dao
interface AssistanceSearchedNeighborhoodDao {
    @Upsert
    suspend fun insertOrUpdate(neighborhood: AssistanceSearchedNeighborhoodEntity)
}
