package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.LeadTenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadTenantDao {
    @Upsert
    suspend fun insertOrUpdate(tenant: LeadTenantEntity): Long

    @Query("UPDATE lead_tenants SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM lead_tenants_read_view WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): LeadTenantReadView

    @Transaction
    @Query(
        """
        SELECT * FROM lead_tenants_read_view
        WHERE status = 0
        AND is_deleted = 0
        AND (
            :matching IS NULL OR
            name LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            city LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY
            CASE WHEN :order = 1 THEN name END COLLATE NOCASE,
            CASE WHEN :order = 2 THEN update_date_time END COLLATE NOCASE DESC
    """
    )
    suspend fun getAllLeadsMatching(matching: String?, order: Int): List<LeadTenantReadView>

    @Transaction
    @Query(
        """
        SELECT * FROM lead_tenants_read_view
        WHERE status <> 0
        AND is_deleted = 0
        AND (
            :matching IS NULL OR
            name LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            city LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY
            CASE WHEN :order = 1 THEN name END COLLATE NOCASE,
            CASE WHEN :order = 2 THEN update_date_time END COLLATE NOCASE DESC
    """
    )
    suspend fun getAllTenantsMatching(matching: String?, order: Int): List<LeadTenantReadView>

    @Transaction
    @Query(
        """
        SELECT * FROM lead_tenants_read_view
        WHERE (status <> 1 OR (:currentTenantId IS NOT NULL AND id = :currentTenantId))
        AND is_deleted = 0
        AND (
            :matching IS NULL OR
            name LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            city LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY name
    """
    )
    suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): List<LeadTenantReadView>

    @Query("SELECT name FROM lead_tenants_read_view WHERE id = :id AND is_deleted = 0")
    suspend fun getNameById(id: Long): String?

    @Query(
        """
        SELECT DISTINCT(tag) FROM lead_tenants_read_view
        WHERE tag IS NOT NULL
        AND is_deleted = 0
        ORDER BY tag
    """
    )
    suspend fun getAllTagNames(): List<String>

    @Query(
        """
        SELECT DISTINCT(city) FROM lead_tenants_read_view
        WHERE city IS NOT NULL
        AND is_deleted = 0
        ORDER BY city
    """
    )
    suspend fun getAllCityNames(): List<String>

    @Query(
        """
        SELECT DISTINCT(contact_origin) FROM lead_tenants_read_view
        WHERE contact_origin IS NOT NULL
        AND is_deleted = 0
        ORDER BY contact_origin
    """
    )
    suspend fun getAllContactOrigins(): List<String>

    @Transaction
    @Query("SELECT * FROM lead_tenants_read_view WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<LeadTenantReadView?>

    @Transaction
    @Query(
        """
        SELECT *
        FROM lead_tenants_read_view t
        WHERE is_deleted = 0
        AND (
            :tenantStatusIdFilter IS NULL OR
            status = :tenantStatusIdFilter
        )
        AND (
            :matching IS NULL OR
            name LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            city LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY
            CASE WHEN :orderTypeId = 1 OR :orderTypeId IS NULL THEN name END COLLATE NOCASE,
            CASE WHEN :orderTypeId = 2 THEN update_date_time END COLLATE NOCASE DESC
    """
    )
    fun watchAll(
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): Flow<List<LeadTenantReadView>>
}
