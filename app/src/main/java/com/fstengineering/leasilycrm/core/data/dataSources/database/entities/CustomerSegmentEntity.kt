package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment

@Entity(
    tableName = "customer_segments",
    foreignKeys = [
        ForeignKey(
            entity = CustomerSegmentTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_segment_type_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("customer_segment_type_id")],
)
data class CustomerSegmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val name: String,
    @ColumnInfo(name = "customer_segment_type_id") val customerSegmentTypeId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
) {
    companion object {
        fun fromDomain(domain: CustomerSegment) = CustomerSegmentEntity(
            id = domain.id,
            name = domain.name!!,
            customerSegmentTypeId = domain.type.id!!,
            userId = domain.user.remoteId!!,
        )
    }
}
