package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType

@Entity(tableName = "property_sea_proximity_types")
data class PropertySeaProximityTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
) {
    fun toDomain() = PropertySeaProximityType(
        id = id,
        name = name,
    )

    companion object {
        fun fromDomain(domain: PropertySeaProximityType) = PropertySeaProximityTypeEntity(
            id = domain.id,
            name = domain.name,
            isActive = domain.isActive,
        )
    }
}
