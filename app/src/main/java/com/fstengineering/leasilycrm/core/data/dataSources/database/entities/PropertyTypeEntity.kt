package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyType

@Entity(tableName = "property_types")
data class PropertyTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
) {
    companion object {
        fun fromDomain(domain: PropertyType) = PropertyTypeEntity(
            id = domain.id,
            name = domain.name,
        )
    }
}
