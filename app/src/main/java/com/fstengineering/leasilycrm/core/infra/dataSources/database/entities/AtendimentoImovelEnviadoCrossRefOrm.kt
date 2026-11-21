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
            entity = AtendimentoOrm::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        ),
        ForeignKey(
            entity = ImovelOrm::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
    ],
    indices = [
        Index("property_id"),
    ],
)
data class AtendimentoImovelEnviadoCrossRefOrm(
    @ColumnInfo(name = "assistance_id") val atendimentoId: Long,
    @ColumnInfo(name = "property_id") val imovelId: Long,
)
