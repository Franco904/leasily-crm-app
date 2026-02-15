package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic

@Entity(tableName = "property_characteristics")
data class PropertyCharacteristicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
) {
    fun toDomain() = PropertyCharacteristic(
        id = id,
        name = name,
        isActive = isActive,
    )

    companion object {
        fun fromDomain(domain: PropertyCharacteristic) =
            PropertyCharacteristicEntity(
                id = domain.id,
                name = domain.name,
                isActive = domain.isActive,
            )
    }
}
