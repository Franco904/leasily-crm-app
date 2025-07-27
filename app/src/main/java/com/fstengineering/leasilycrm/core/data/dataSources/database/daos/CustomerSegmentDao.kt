package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegment

@Dao
interface CustomerSegmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(customerSegment: CustomerSegment): Long

    @Delete
    suspend fun delete(customerSegment: CustomerSegment)

    @Query("SELECT * FROM customer_segments")
    suspend fun getAll(): List<CustomerSegment>
}
