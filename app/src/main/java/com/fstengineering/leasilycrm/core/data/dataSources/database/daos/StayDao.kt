package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity

@Dao
interface StayDao {
    @Upsert
    suspend fun insertOrUpdate(stay: StayEntity): Long

    @Query("SELECT * FROM stays WHERE property_id = :propertyId")
    suspend fun getAllByProperty(propertyId: Long): List<StayEntity>

    @Query("SELECT * FROM stays WHERE tenant_id = :tenantId")
    suspend fun getAllByTenant(tenantId: Long): List<StayEntity>
}
