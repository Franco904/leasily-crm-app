package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compound.CustomerSegmentWithType
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerSegmentDao {
    @Insert
    suspend fun insert(customerSegment: CustomerSegmentEntity)

    @Update
    suspend fun update(customerSegment: CustomerSegmentEntity)

    @Query("DELETE FROM customer_segments WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM customer_segments WHERE id = :id")
    suspend fun getById(id: Int): CustomerSegmentEntity

    @Transaction
    @Query("SELECT * FROM customer_segments WHERE user_id = :userId")
    fun watchAll(userId: Int): Flow<List<CustomerSegmentWithType>>
}
