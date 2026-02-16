package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceSentProperty
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

@Entity(
    tableName = "assistance_sent_properties",
    primaryKeys = ["assistance_id", "property_id"],
    foreignKeys = [
        ForeignKey(
            entity = AssistanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        ),
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
    ],
    indices = [
        Index("assistance_id"),
        Index("property_id"),
    ],
)
data class AssistanceSentPropertyCrossRefEntity(
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
) {
    companion object {
        fun fromDomain(domain: AssistanceSentProperty, assistanceId: Long) =
            AssistanceSentPropertyCrossRefEntity(
                assistanceId = assistanceId,
                propertyId = requireNotNull(domain.property.id),
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                isDeleted = domain.isDeleted,
            )
    }
}
