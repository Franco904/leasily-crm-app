package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic

@Entity(tableName = "property_characteristics")
data class PropertyCharacteristicEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
) {
    companion object {
        fun fromDomain(domain: PropertyCharacteristic) = PropertyCharacteristicEntity(
            id = domain.id,
            name = domain.name,
        )
    }
}
