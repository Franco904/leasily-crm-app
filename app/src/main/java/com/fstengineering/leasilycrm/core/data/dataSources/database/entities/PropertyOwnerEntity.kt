package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "property_owners",
    foreignKeys = [
        ForeignKey(
            entity = CustomerSegmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_segment_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("customer_segment_id")],
)
data class PropertyOwnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String?,
    val email: String?,
    @ColumnInfo(name = "payment_key") val paymentKey: String?,
    @ColumnInfo(name = "customer_segment_id") val customerSegmentId: Int?,
    @ColumnInfo(name = "user_id") val userId: Int,
)
