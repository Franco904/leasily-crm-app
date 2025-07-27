package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_segment_types")
data class CustomerSegmentType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: Int,
)
