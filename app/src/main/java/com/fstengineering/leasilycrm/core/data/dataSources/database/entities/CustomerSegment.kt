package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customer_segments",
    foreignKeys = [
        ForeignKey(
            entity = CustomerSegmentType::class,
            parentColumns = ["id"],
            childColumns = ["customer_segment_type_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("customer_segment_type_id")],
)
data class CustomerSegment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "customer_segment_type_id") val customerSegmentTypeId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
)
