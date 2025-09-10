package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity

@Dao
interface AssistanceSearchedNeighborhoodDao {
    @Upsert
    suspend fun insertOrUpdate(neighborhood: AssistanceSearchedNeighborhoodEntity): Long

    @Query("SELECT * FROM assistance_searched_neighborhoods WHERE assistance_id = :assistanceId")
    suspend fun getByAssistance(assistanceId: Long): List<AssistanceSearchedNeighborhoodEntity>

    @Query("SELECT * FROM assistance_searched_neighborhoods")
    suspend fun getAll(): List<AssistanceSearchedNeighborhoodEntity>
}
