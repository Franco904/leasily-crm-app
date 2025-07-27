package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhood

@Dao
interface AssistanceSearchedNeighborhoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(neighborhood: AssistanceSearchedNeighborhood): Long

    @Query("SELECT * FROM assistance_searched_neighborhoods WHERE assistance_id = :assistanceId")
    suspend fun getByAssistance(assistanceId: Int): List<AssistanceSearchedNeighborhood>

    @Query("SELECT * FROM assistance_searched_neighborhoods")
    suspend fun getAll(): List<AssistanceSearchedNeighborhood>
}
