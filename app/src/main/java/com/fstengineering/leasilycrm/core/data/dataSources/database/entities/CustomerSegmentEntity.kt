package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding

@Entity(tableName = "customer_segments")
data class CustomerSegmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val name: String,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    fun toDomain() = CustomerSegment(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        name = name,
        isDeleted = isDeleted,
        userId = userId,
    )

    companion object {
        fun fromDomain(domain: CustomerSegment): CustomerSegmentEntity {
            requireNotNull(domain.userId) { "CustomerSegment.userId is required" }
            requireNotNull(domain.name) { "CustomerSegment.name is required" }

            return CustomerSegmentEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                name = domain.name.normalizeEncoding(),
                userId = domain.userId,
                isDeleted = domain.isDeleted,
            )
        }
    }
}
