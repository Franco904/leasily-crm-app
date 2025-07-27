package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginType

@Dao
interface TenantContactOriginTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(tenantContactOriginType: TenantContactOriginType): Long

    @Query("SELECT * FROM tenant_contact_origin_types")
    suspend fun getAll(): List<TenantContactOriginType>
}
