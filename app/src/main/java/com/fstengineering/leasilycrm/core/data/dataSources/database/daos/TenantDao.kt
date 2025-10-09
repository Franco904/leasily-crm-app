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

    @Query("SELECT * FROM tenants WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): TenantCompound

    @Transaction
    @Query(
        """
        SELECT * FROM tenants
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
        SELECT * FROM tenants
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
        SELECT * FROM tenants
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

    @Query("SELECT name FROM tenants WHERE id = :id AND is_deleted = 0")
    suspend fun getNameById(id: Long): String

    @Query(
        """
        SELECT DISTINCT(city) FROM tenants
        WHERE user_id = :userId
        AND city IS NOT NULL
        AND is_deleted = 0
        ORDER BY city
    """
    )
    suspend fun getAllCityNames(userId: Long): List<String>

    @Transaction
    @Query("SELECT * FROM tenants WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<TenantCompound?>

    @Transaction
    @Query(
        """
        SELECT * FROM tenants
        WHERE user_id = :userId
        AND is_deleted = 0
    """
    )
    fun watchAll(userId: Long): Flow<List<TenantCompound>>

    @Transaction
    @Query(
        """
        SELECT * FROM tenants
        WHERE user_id = :userId
        AND status = 0
        AND is_deleted = 0
        ORDER BY
            CASE WHEN :order = 1 THEN name END COLLATE NOCASE,
            CASE WHEN :order = 2 THEN update_date_time END COLLATE NOCASE DESC
    """
    )
    fun watchAllLeads(userId: Long, order: Int): Flow<List<TenantCompound>>

    @Transaction
    @Query(
        """
        SELECT * FROM tenants
        WHERE user_id = :userId
        AND status <> 0
        AND is_deleted = 0
        AND (
            :tenantFilterType IS NULL OR
            status = :tenantFilterType
        )
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
    fun watchAllTenants(
        userId: Long,
        order: Int,
        tenantFilterType: Int?,
        matching: String?,
    ): Flow<List<TenantCompound>>

    @Query(
        """
        SELECT COUNT(name) FROM tenants
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
