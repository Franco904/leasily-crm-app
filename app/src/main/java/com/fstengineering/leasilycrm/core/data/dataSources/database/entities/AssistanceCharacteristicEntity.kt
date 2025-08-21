package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic

@Entity(tableName = "assistance_characteristics")
data class AssistanceCharacteristicEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
) {
    companion object {
        fun fromDomain(domain: AssistanceCharacteristic) = AssistanceCharacteristicEntity(
            id = domain.id,
            name = domain.name,
        )
    }
}
