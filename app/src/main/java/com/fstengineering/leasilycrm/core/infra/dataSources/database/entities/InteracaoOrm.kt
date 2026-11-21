package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

@Entity(
    tableName = "assistance_interactions",
    foreignKeys = [
        ForeignKey(
            entity = AtendimentoOrm::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        ),
    ],
    indices = [Index("assistance_id")],
)
data class InteracaoOrm(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val date: AppDate,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "assistance_id") val atendimentoId: Long,
)
