package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType

@Entity(tableName = "property_sea_proximity_types")
data class PropertySeaProximityTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
) {
    fun toDomain() = PropertySeaProximityType(
        id = id,
        name = name,
        isActive = isActive,
    )

    companion object {
        fun fromDomain(domain: PropertySeaProximityType) =
            PropertySeaProximityTypeEntity(
                id = domain.id,
                name = domain.name,
                isActive = domain.isActive,
            )
    }
}
