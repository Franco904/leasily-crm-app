package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristicAssistance

@Entity(
    tableName = "assistance_characteristics_assistances",
    primaryKeys = ["assistance_id", "assistance_characteristic_id"],
    foreignKeys = [
        ForeignKey(
            entity = AssistanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        ),
        ForeignKey(
            entity = AssistanceCharacteristicEntity::class,
            parentColumns = ["id"],
            childColumns = ["assistance_characteristic_id"],
        ),
    ],
    indices = [
        Index("assistance_id"),
        Index("assistance_characteristic_id"),
    ],
)
data class AssistanceCharacteristicAssistanceCrossRefEntity(
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
    @ColumnInfo(name = "assistance_characteristic_id") val assistanceCharacteristicId: Long,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    companion object {
        fun fromDomain(
            domain: AssistanceCharacteristicAssistance,
        ): AssistanceCharacteristicAssistanceCrossRefEntity {
            requireNotNull(domain.assistanceId) { "AssistanceCharacteristicAssistance.assistanceId is required" }
            requireNotNull(domain.assistanceCharacteristic?.id) { "AssistanceCharacteristicAssistance.assistanceCharacteristic.id is required" }

            return AssistanceCharacteristicAssistanceCrossRefEntity(
                assistanceId = domain.assistanceId,
                assistanceCharacteristicId = domain.assistanceCharacteristic.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                isDeleted = domain.isDeleted,
            )
        }
    }
}
