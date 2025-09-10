package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CustomerSegmentCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerSegmentDao {
    @Upsert
    suspend fun insertOrUpdate(customerSegment: CustomerSegmentEntity)

    @Query("UPDATE customer_segments SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM customer_segments WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): CustomerSegmentEntity

    @Transaction
    @Query(
        """
        SELECT * FROM customer_segments
        WHERE user_id = :userId
        AND customer_segment_type_id = 1
        AND is_deleted = 0
        ORDER BY name
    """
    )
    suspend fun getAllTenantType(userId: Long): List<CustomerSegmentCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM customer_segments
        WHERE user_id = :userId
        AND customer_segment_type_id = 2
        AND is_deleted = 0
        ORDER BY name
    """
    )
    suspend fun getAllPropertyOwnerType(userId: Long): List<CustomerSegmentCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM customer_segments
        WHERE user_id = :userId
        AND is_deleted = 0
        ORDER BY name
    """
    )
    fun watchAll(userId: Long): Flow<List<CustomerSegmentCompound>>
}
