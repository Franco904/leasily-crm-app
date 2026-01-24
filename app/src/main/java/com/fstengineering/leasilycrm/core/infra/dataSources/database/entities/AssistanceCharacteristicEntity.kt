package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic

@Entity(tableName = "assistance_characteristics")
data class AssistanceCharacteristicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
) {
    fun toDomain() = AssistanceCharacteristic(
        id = id,
        name = name,
    )

    companion object {
        fun fromDomain(domain: AssistanceCharacteristic) = AssistanceCharacteristicEntity(
            id = domain.id,
            name = domain.name,
            isActive = domain.isActive,
        )
    }
}
