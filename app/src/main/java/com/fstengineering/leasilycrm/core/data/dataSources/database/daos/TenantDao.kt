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
    @Query("SELECT * FROM tenants WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<TenantCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM tenants
        WHERE user_id = :userId
        AND is_deleted = 0
        ORDER BY name
    """
    )
    fun watchAll(userId: Long): Flow<List<TenantCompound>>
}