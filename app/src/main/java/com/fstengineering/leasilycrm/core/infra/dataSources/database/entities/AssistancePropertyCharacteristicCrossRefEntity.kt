package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

@Entity(
    tableName = "assistance_property_characteristics",
    primaryKeys = ["assistance_id", "property_characteristic_id"],
    foreignKeys = [
        ForeignKey(
            entity = AssistanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        ),
    ],
)
data class AssistancePropertyCharacteristicCrossRefEntity(
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
    @ColumnInfo(name = "property_characteristic_id") val propertyCharacteristicId: Long,
) {
    companion object {
        fun fromDomain(propertyCharacteristicId: Long, assistanceId: Long) =
            AssistancePropertyCharacteristicCrossRefEntity(
                assistanceId = assistanceId,
                propertyCharacteristicId = propertyCharacteristicId,
            )
    }
}
