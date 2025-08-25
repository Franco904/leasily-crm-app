package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

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
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("assistance_id"),
        Index("property_id"),
    ],
)
data class AssistanceSentPropertyCrossRefEntity(
    @ColumnInfo(name = "assistance_id") val assistanceId: Int,
    @ColumnInfo(name = "property_id") val propertyId: Int,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
)
