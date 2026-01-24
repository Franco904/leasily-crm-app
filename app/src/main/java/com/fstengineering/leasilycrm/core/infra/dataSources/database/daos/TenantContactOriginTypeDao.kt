package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantContactOriginTypeEntity

@Dao
interface TenantContactOriginTypeDao {
    @Upsert
    suspend fun insertOrUpdate(tenantContactOriginType: TenantContactOriginTypeEntity)

    @Query("SELECT * FROM tenant_contact_origin_types")
    suspend fun getAll(): List<TenantContactOriginTypeEntity>
}
