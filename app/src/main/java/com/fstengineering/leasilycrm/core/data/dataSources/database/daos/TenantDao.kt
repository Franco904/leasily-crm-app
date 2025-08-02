package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity

@Dao
interface TenantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(tenant: TenantEntity): Long

    @Delete
    suspend fun delete(tenant: TenantEntity)

    @Query("SELECT * FROM tenants")
    suspend fun getAll(): List<TenantEntity>
}