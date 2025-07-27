package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "property_characteristics_properties",
    primaryKeys = ["property_id", "property_characteristic_id"],
    foreignKeys = [
        ForeignKey(
            entity = Property::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PropertyCharacteristic::class,
            parentColumns = ["id"],
            childColumns = ["property_characteristic_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("property_id"),
        Index("property_characteristic_id"),
    ],
)
data class PropertyCharacteristicPropertyCrossRef(
    @ColumnInfo(name = "property_id") val propertyId: Int,
    @ColumnInfo(name = "property_characteristic_id") val propertyCharacteristicId: Int,
)
