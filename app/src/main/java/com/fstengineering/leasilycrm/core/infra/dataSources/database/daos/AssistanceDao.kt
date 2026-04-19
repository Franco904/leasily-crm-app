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

    @Query("UPDATE assistances SET is_finished = 1 WHERE lead_tenant_id = :tenantId AND is_finished = 0 AND is_deleted = 0")
    suspend fun finishOpenAssistanceByTenantId(tenantId: Long)

    @Query("UPDATE assistances SET is_finished = 0 WHERE lead_tenant_id = :tenantId AND is_finished = 1 AND is_deleted = 0")
    suspend fun unfinishAssistanceByTenantId(tenantId: Long)

    @Transaction
    @Query("SELECT * FROM assistances WHERE lead_tenant_id = :leadTenantId AND is_deleted = 0")
    suspend fun getAllByLeadTenantId(leadTenantId: Long): List<AssistanceCompound>

    @Query(
        """
        SELECT DISTINCT(neighborhood) FROM assistance_preferred_neighborhoods apn
        INNER JOIN assistances a ON a.id = apn.assistance_id
        INNER JOIN lead_tenants_read_view lt ON lt.id = a.lead_tenant_id
        ORDER BY neighborhood
    """
    )
    suspend fun getAllNeighborhoodNames(): List<String>
}
