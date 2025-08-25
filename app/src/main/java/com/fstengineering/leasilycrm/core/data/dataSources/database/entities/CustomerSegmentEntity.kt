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
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index("customer_segment_type_id"),
        Index("user_id"),
    ],
)
data class CustomerSegmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val name: String,
    @ColumnInfo(name = "customer_segment_type_id") val customerSegmentTypeId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    companion object {
        fun fromDomain(domain: CustomerSegment): CustomerSegmentEntity {
            requireNotNull(domain.type.id) { "CustomerSegment.type.id is required" }
            requireNotNull(domain.user.remoteId) { "CustomerSegment.user.remoteId is required" }
            requireNotNull(domain.name) { "CustomerSegment.name is required" }

            return CustomerSegmentEntity(
                id = domain.id,
                name = domain.name,
                customerSegmentTypeId = domain.type.id,
                userId = domain.user.remoteId,
                isDeleted = false,
            )
        }
    }
}
