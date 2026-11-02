package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "property_characteristics_properties",
    primaryKeys = ["property_id", "property_characteristic_id"],
    foreignKeys = [
        ForeignKey(
            entity = ImovelOrm::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
    ],
)
data class ImovelCharacteristicCrossRefOrm(
    @ColumnInfo(name = "property_id") val imovelId: Long,
    @ColumnInfo(name = "property_characteristic_id") val imovelCharacteristicId: Long,
)
