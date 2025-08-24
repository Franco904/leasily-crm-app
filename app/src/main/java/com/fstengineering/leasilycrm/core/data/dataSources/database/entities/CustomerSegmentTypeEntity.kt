package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType

@Entity(tableName = "customer_segment_types")
data class CustomerSegmentTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
) {
    fun toDomain() = CustomerSegmentType(
        id = id,
        name = name,
    )

    companion object {
        fun fromDomain(domain: CustomerSegmentType) = CustomerSegmentTypeEntity(
            id = domain.id,
            name = domain.name!!,
        )
    }
}
