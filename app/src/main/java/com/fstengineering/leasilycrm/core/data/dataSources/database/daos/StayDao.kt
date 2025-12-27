package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCheckStatusCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCompound
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
    @Query("SELECT * FROM stays WHERE user_id = :userId AND is_deleted = 0")
    fun watchAll(userId: Long): Flow<List<StayCompound>>

    @Query(
        """
        SELECT 
            t.name AS tenant_name, 
            pt.name AS property_type_name, 
            po.name AS property_owner_name, 
            s.check_in_date_time, 
            s.check_out_date_time, 
            s.is_check_in_done
        FROM stays s
        LEFT JOIN tenants_read_view t ON s.tenant_id = t.id AND t.is_deleted = 0
        LEFT JOIN properties p ON s.property_id = p.id AND p.is_deleted = 0
        LEFT JOIN property_owners po ON p.property_owner_id = po.id AND po.is_deleted = 0
        LEFT JOIN property_types pt ON p.property_type_id = pt.id AND pt.is_active = 1
        WHERE s.is_check_out_done = 0
        AND s.is_from_other_realtor = 0
        AND s.is_deleted = 0
        AND s.user_id = :userId
    """
    )
    fun watchAllUpcomingCheckInsAndCheckOuts(
        userId: Long,
    ): Flow<List<StayCheckStatusCompound>>
}
