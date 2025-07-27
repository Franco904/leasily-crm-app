package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "assistance_characteristics_assistances",
    primaryKeys = ["assistance_id", "assistance_characteristic_id"],
    foreignKeys = [
        ForeignKey(
            entity = Assistance::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = AssistanceCharacteristic::class,
            parentColumns = ["id"],
            childColumns = ["assistance_characteristic_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("assistance_id"),
        Index("assistance_characteristic_id"),
    ],
)
data class AssistanceCharacteristicAssistanceCrossRef(
    @ColumnInfo(name = "assistance_id") val assistanceId: Int,
    @ColumnInfo(name = "assistance_characteristic_id") val assistanceCharacteristicId: Int,
)
