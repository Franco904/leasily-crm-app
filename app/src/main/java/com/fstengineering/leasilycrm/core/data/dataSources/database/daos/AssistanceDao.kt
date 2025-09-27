package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.AssistanceCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface AssistanceDao {
    @Upsert
    suspend fun insertOrUpdate(assistance: AssistanceEntity): Long

    @Query("UPDATE assistances SET is_deleted = 1, is_finished = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM assistances WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): AssistanceCompound

    @Query(
        """
        SELECT DISTINCT(neighborhood) FROM assistance_searched_neighborhoods asn
        INNER JOIN assistances a ON a.id = asn.assistance_id
        INNER JOIN tenants t ON t.id = a.tenant_id
        WHERE t.user_id = :userId
        AND asn.is_deleted = 0
        ORDER BY neighborhood
    """
    )
    suspend fun getAllNeighborhoodNames(userId: Long): List<String>

    @Transaction
    @Query("SELECT * FROM assistances WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<AssistanceCompound?>

    @Transaction
    @Query(
        """
        SELECT * FROM assistances
        WHERE tenant_id = :tenantId
        AND is_deleted = 0
        ORDER BY id DESC
    """
    )
    fun watchAllByTenant(tenantId: Long): Flow<List<AssistanceCompound>> // TODO: Order by register date DESC
}
