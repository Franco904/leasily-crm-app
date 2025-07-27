package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.Stay

@Dao
interface StayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stay: Stay): Long

    @Query("SELECT * FROM stays WHERE property_id = :propertyId")
    suspend fun getAllByProperty(propertyId: Int): List<Stay>

    @Query("SELECT * FROM stays WHERE tenant_id = :tenantId")
    suspend fun getAllByTenant(tenantId: Int): List<Stay>
}
