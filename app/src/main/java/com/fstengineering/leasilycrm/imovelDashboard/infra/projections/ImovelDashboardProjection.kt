package com.fstengineering.leasilycrm.imovelDashboard.infra.projections

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm

/**
 * Imóvel do painel com o Proprietário, lido numa única transação. A relação traz também o
 * Proprietário excluído; o mapper decide o que fazer com ele.
 */
data class ImovelDashboardProjection(
    val id: Long,
    /** Coluna-pai da relação [proprietario]; o Room exige que ela venha na consulta. */
    val proprietarioId: Long,
    val typeId: Long,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val mainPictureUriString: String?,
    @Relation(
        parentColumn = "proprietarioId",
        entityColumn = "id",
        entity = ProprietarioOrm::class,
        projection = ["id", "name", "is_deleted"],
    )
    val proprietario: ProprietarioProjection?,
)

data class ProprietarioProjection(
    val id: Long,
    val name: String,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
)
