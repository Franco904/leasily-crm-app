package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentTypeEntity

@Dao
interface CustomerSegmentTypeDao {
    @Upsert
    suspend fun insertOrUpdate(customerSegmentType: CustomerSegmentTypeEntity): Long

    @Query("SELECT * FROM customer_segment_types WHERE is_active = 1")
    suspend fun getAll(): List<CustomerSegmentTypeEntity>
}
