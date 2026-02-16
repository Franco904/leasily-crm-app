package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound

@Dao
interface AssistanceDao {
    @Upsert
    suspend fun insertOrUpdate(assistance: AssistanceEntity): Long

    @Query("UPDATE assistances SET is_deleted = 1, is_finished = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM assistances WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): AssistanceCompound

    @Query(
        """
        SELECT DISTINCT(neighborhood) FROM assistance_searched_neighborhoods asn
        INNER JOIN assistances a ON a.id = asn.assistance_id
        INNER JOIN tenants_read_view t ON t.id = a.tenant_id
        WHERE asn.is_deleted = 0
        ORDER BY neighborhood
    """
    )
    suspend fun getAllNeighborhoodNames(): List<String>
}
