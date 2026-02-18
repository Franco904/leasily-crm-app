package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fstengineering.leasilycrm.core.domain.entities.AssistancePropertyCharacteristic
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
    indices = [
        Index("assistance_id"),
        Index("property_characteristic_id"),
    ],
)
data class AssistancePropertyCharacteristicCrossRefEntity(
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
    @ColumnInfo(name = "property_characteristic_id") val propertyCharacteristicId: Long,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime, // TODO: Faz sentido createDateTime e updateDateTime aqui?
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
) {
    companion object {
        fun fromDomain(domain: AssistancePropertyCharacteristic, assistanceId: Long) =
            AssistancePropertyCharacteristicCrossRefEntity(
                assistanceId = assistanceId,
                propertyCharacteristicId = domain.propertyCharacteristic.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                isDeleted = false,
            )
    }
}
