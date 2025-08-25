package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType

@Entity(tableName = "customer_segment_types")
data class CustomerSegmentTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
) {
    fun toDomain() = CustomerSegmentType(
        id = id,
        name = name,
    )

    companion object {
        fun fromDomain(domain: CustomerSegmentType): CustomerSegmentTypeEntity {
            requireNotNull(domain.name) { "CustomerSegmentType.name is required" }

            return CustomerSegmentTypeEntity(
                id = domain.id,
                name = domain.name,
                isActive = domain.isActive,
            )
        }
    }
}
