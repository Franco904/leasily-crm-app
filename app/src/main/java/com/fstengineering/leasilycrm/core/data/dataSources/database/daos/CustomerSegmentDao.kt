package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity

@Dao
interface CustomerSegmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(customerSegment: CustomerSegmentEntity): Long

    @Delete
    suspend fun delete(customerSegment: CustomerSegmentEntity)

    @Query("SELECT * FROM customer_segments")
    suspend fun getAll(): List<CustomerSegmentEntity>
}
