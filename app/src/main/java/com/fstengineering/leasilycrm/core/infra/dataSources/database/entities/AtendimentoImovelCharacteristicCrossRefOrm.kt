package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "assistance_property_characteristics",
    primaryKeys = ["assistance_id", "property_characteristic_id"],
    foreignKeys = [
        ForeignKey(
            entity = AtendimentoOrm::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        ),
    ],
)
data class AtendimentoImovelCharacteristicCrossRefOrm(
    @ColumnInfo(name = "assistance_id") val atendimentoId: Long,
    @ColumnInfo(name = "property_characteristic_id") val imovelCharacteristicId: Long,
)
