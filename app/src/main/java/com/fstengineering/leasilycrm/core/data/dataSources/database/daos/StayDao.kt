package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface StayDao {
    @Upsert
    suspend fun insertOrUpdate(stay: StayEntity): Long

    @Query("UPDATE stays SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM stays WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): StayCompound

    @Transaction
    @Query("SELECT * FROM stays WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<StayCompound?>

    @Query("SELECT * FROM stays WHERE user_id = :userId")
    suspend fun watchAll(userId: Long): Flow<List<StayCompound>>

    @Query("SELECT * FROM stays WHERE property_id = :propertyId")
    suspend fun watchAllByProperty(propertyId: Long): Flow<List<StayCompound>>

    @Query("SELECT * FROM stays WHERE tenant_id = :tenantId")
    suspend fun watchAllByTenant(tenantId: Long): Flow<List<StayCompound>>
}
