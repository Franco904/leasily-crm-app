package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

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
        Index("property_id"),
    ],
)
data class AssistanceSentPropertyCrossRefEntity(
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
    @ColumnInfo(name = "property_id") val propertyId: Long,
) {
    companion object {
        fun fromDomain(propertyId: Long, assistanceId: Long) =
            AssistanceSentPropertyCrossRefEntity(
                assistanceId = assistanceId,
                propertyId = propertyId,
            )
    }
}
