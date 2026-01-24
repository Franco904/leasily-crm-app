package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.TenantCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface TenantDao {
    @Upsert
    suspend fun insertOrUpdate(tenant: TenantEntity): Long

    @Query("UPDATE tenants SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM tenants_read_view WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): TenantCompound?

    @Transaction
    @Query(
        """
        SELECT * FROM tenants_read_view
        WHERE user_id = :userId
        AND status = 0
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
    suspend fun getAllLeadsMatching(
        userId: Long,
        matching: String?,
        order: Int,
    ): List<TenantCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM tenants_read_view
        WHERE user_id = :userId
        AND status <> 0
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
    suspend fun getAllTenantsMatching(
        userId: Long,
        matching: String?,
        order: Int,
    ): List<TenantCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM tenants_read_view
        WHERE user_id = :userId
        AND (status <> 1 OR (:currentTenantId IS NOT NULL AND id = :currentTenantId))
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
        userId: Long,
        matching: String?,
        currentTenantId: Long?,
    ): List<TenantCompound>

    @Query("SELECT name FROM tenants_read_view WHERE id = :id AND is_deleted = 0")
    suspend fun getNameById(id: Long): String

    @Query(
        """
        SELECT DISTINCT(tag) FROM tenants_read_view
        WHERE user_id = :userId
        AND tag IS NOT NULL
        AND is_deleted = 0
        ORDER BY tag
    """
    )
    suspend fun getAllTagNames(userId: Long): List<String>

    @Query(
        """
        SELECT DISTINCT(city) FROM tenants_read_view
        WHERE user_id = :userId
        AND city IS NOT NULL
        AND is_deleted = 0
        ORDER BY city
    """
    )
    suspend fun getAllCityNames(userId: Long): List<String>

    @Transaction
    @Query("SELECT * FROM tenants_read_view WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<TenantCompound?>

    @Transaction
    @Query(
        """
        SELECT *
        FROM tenants_read_view t
        WHERE user_id = :userId
        AND is_deleted = 0
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
        userId: Long,
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): Flow<List<TenantCompound>>

    @Query(
        """
        SELECT COUNT(name) FROM tenants_read_view
        WHERE user_id = :userId
        AND (
            name = :tenantName AND (:currentStoredTenantName IS NULL OR name <> :currentStoredTenantName)
        )
        AND is_deleted = 0
    """
    )
    suspend fun countTenantsWithName(
        userId: Long,
        tenantName: String,
        currentStoredTenantName: String?,
    ): Int
}
