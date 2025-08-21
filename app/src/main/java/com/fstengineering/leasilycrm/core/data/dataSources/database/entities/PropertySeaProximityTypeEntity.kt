package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType

@Entity(tableName = "property_sea_proximity_types")
data class PropertySeaProximityTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
) {
    companion object {
        fun fromDomain(domain: PropertySeaProximityType) = PropertySeaProximityTypeEntity(
            id = domain.id,
            name = domain.name,
        )
    }
}
