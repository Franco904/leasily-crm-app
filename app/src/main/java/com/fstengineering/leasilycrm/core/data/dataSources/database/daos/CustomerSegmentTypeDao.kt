package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentType

@Dao
interface CustomerSegmentTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(customerSegmentType: CustomerSegmentType): Long

    @Query("SELECT * FROM customer_segment_types")
    suspend fun getAll(): List<CustomerSegmentType>
}
