package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner

@Entity(
    tableName = "property_owners",
    foreignKeys = [
        ForeignKey(
            entity = CustomerSegmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_segment_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index("customer_segment_id"),
        Index("user_id"),
    ],
)
data class PropertyOwnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String?,
    val email: String?,
    @ColumnInfo(name = "payment_key") val paymentKey: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "customer_segment_id") val customerSegmentId: Int?,
    @ColumnInfo(name = "user_id") val userId: Int,
) {
    companion object {
        fun fromDomain(domain: PropertyOwner): PropertyOwnerEntity {
            requireNotNull(domain.id) { "PropertyOwner.id is required" }
            requireNotNull(domain.name) { "PropertyOwner.name is required" }

            return PropertyOwnerEntity(
                id = domain.id,
                name = domain.name,
                phone = domain.phone,
                email = domain.email,
                paymentKey = domain.paymentKey,
                customerSegmentId = domain.customerSegment?.id,
                userId = domain.userId,
            )
        }
    }
}
