package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity

@Dao
interface TenantContactOriginTypeDao {
    @Upsert
    suspend fun insertOrUpdate(tenantContactOriginType: TenantContactOriginTypeEntity): Long

    @Query("SELECT * FROM tenant_contact_origin_types")
    suspend fun getAll(): List<TenantContactOriginTypeEntity>
}
