package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity

@Dao
interface TenantDao {
    @Upsert
    suspend fun insertOrUpdate(tenant: TenantEntity): Long

    @Delete
    suspend fun delete(tenant: TenantEntity)

    @Query("SELECT * FROM tenants")
    suspend fun getAll(): List<TenantEntity>
}