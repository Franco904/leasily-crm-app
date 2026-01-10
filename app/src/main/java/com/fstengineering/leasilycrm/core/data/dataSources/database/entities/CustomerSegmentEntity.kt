package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding

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
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val name: String,
    @ColumnInfo(name = "customer_segment_type_id") val customerSegmentTypeId: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    companion object {
        fun fromDomain(domain: CustomerSegment): CustomerSegmentEntity {
            requireNotNull(domain.type?.id) { "CustomerSegment.type.id is required" }
            requireNotNull(domain.userId) { "CustomerSegment.userId is required" }
            requireNotNull(domain.name) { "CustomerSegment.name is required" }

            return CustomerSegmentEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                name = domain.name.normalizeEncoding(),
                customerSegmentTypeId = domain.type.id,
                userId = domain.userId,
                isDeleted = domain.isDeleted,
            )
        }
    }
}
