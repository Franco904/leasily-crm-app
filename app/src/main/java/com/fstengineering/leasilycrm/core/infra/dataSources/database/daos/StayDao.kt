package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCheckStatusCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface StayDao {
    @Upsert
    suspend fun insertOrUpdate(stay: StayEntity): Long

    @Query("UPDATE stays SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM stays WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): StayCompound

    @Transaction
    @Query("SELECT * FROM stays WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<StayCompound?>

    @Transaction
    @Query("SELECT * FROM stays WHERE is_deleted = 0")
    fun watchAll(): Flow<List<StayCompound>>

    @Query("SELECT * FROM stays WHERE property_id = :propertyId AND is_deleted = 0")
    fun watchAllByPropertyId(propertyId: Long): Flow<List<StayEntity>>

    @Query(
        """
        SELECT 
            t.name AS tenant_name, 
            p.property_type_id AS property_type_id, 
            po.name AS property_owner_name, 
            s.check_in_date_time, 
            s.check_out_date_time, 
            s.status
        FROM stays s
        LEFT JOIN lead_tenants_read_view t ON s.tenant_id = t.id AND t.is_deleted = 0
        LEFT JOIN properties p ON s.property_id = p.id AND p.is_deleted = 0
        LEFT JOIN property_owners po ON p.property_owner_id = po.id AND po.is_deleted = 0
        WHERE (s.status = 0 OR s.status = 1)
        AND s.tenant_id IS NOT NULL
        AND s.is_deleted = 0
    """
    )
    fun watchAllUpcomingCheckInsAndCheckOuts(): Flow<List<StayCheckStatusCompound>>
}
