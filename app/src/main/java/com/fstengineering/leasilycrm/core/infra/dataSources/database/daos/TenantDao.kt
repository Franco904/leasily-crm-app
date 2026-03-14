package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.TenantReadView
import kotlinx.coroutines.flow.Flow

@Dao
interface TenantDao {
    @Upsert
    suspend fun insertOrUpdate(tenant: TenantEntity): Long

    @Query("UPDATE tenants SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM tenants_read_view WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): TenantReadView

    @Transaction
    @Query(
        """
        SELECT * FROM tenants_read_view
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
    suspend fun getAllLeadsMatching(matching: String?, order: Int): List<TenantReadView>

    @Transaction
    @Query(
        """
        SELECT * FROM tenants_read_view
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
    suspend fun getAllTenantsMatching(matching: String?, order: Int): List<TenantReadView>

    @Transaction
    @Query(
        """
        SELECT * FROM tenants_read_view
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
    ): List<TenantReadView>

    @Query("SELECT name FROM tenants_read_view WHERE id = :id AND is_deleted = 0")
    suspend fun getNameById(id: Long): String?

    @Query(
        """
        SELECT DISTINCT(tag) FROM tenants_read_view
        WHERE tag IS NOT NULL
        AND is_deleted = 0
        ORDER BY tag
    """
    )
    suspend fun getAllTagNames(): List<String>

    @Query(
        """
        SELECT DISTINCT(city) FROM tenants_read_view
        WHERE city IS NOT NULL
        AND is_deleted = 0
        ORDER BY city
    """
    )
    suspend fun getAllCityNames(): List<String>

    @Query(
        """
        SELECT DISTINCT(contact_origin) FROM tenants_read_view
        WHERE contact_origin IS NOT NULL
        AND is_deleted = 0
        ORDER BY contact_origin
    """
    )
    suspend fun getAllContactOrigins(): List<String>

    @Transaction
    @Query("SELECT * FROM tenants_read_view WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<TenantReadView?>

    @Transaction
    @Query(
        """
        SELECT *
        FROM tenants_read_view t
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
    ): Flow<List<TenantReadView>>
}
