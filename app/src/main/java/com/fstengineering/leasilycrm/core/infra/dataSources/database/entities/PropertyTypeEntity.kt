package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyType

@Entity(tableName = "property_types")
data class PropertyTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
) {
    fun toDomain() = PropertyType(
        id = id,
        name = name,
        isActive = isActive,
    )

    companion object {
        fun fromDomain(domain: PropertyType) =
            PropertyTypeEntity(
                id = domain.id,
                name = domain.name,
                isActive = domain.isActive,
            )
    }
}
