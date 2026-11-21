package com.fstengineering.leasilycrm.clienteDetails.infra.projections

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceImovelCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentImovelCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity

/**
 * Cliente da SCV com tudo que a tela exibe, lido numa única transação: colunas da view de leitura
 * e as relações (Atendimentos, Imóveis enviados e Estadias) que o Room carrega em lote. As relações
 * trazem também os registros excluídos; o mapper descarta esses.
 */
data class ClienteDetailsProjection(
    val id: Long,
    val tag: String?,
    val name: String,
    val city: String?,
    @ColumnInfo(name = "identity_document") val identityDocument: String?,
    val phone: String?,
    val email: String?,
    @ColumnInfo(name = "contact_origin") val contactOrigin: String?,
    val observations: String?,
    val status: Int,
    @Relation(
        parentColumn = "id",
        entityColumn = "lead_tenant_id",
        entity = AssistanceEntity::class,
    )
    val atendimentos: List<AtendimentoProjection>,
    @Relation(
        parentColumn = "id",
        entityColumn = "tenant_id",
        entity = StayEntity::class,
    )
    val estadias: List<StayEntity>,
)

data class AtendimentoProjection(
    @Embedded val assistance: AssistanceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistancePreferredNeighborhoodEntity::class,
    )
    val preferredNeighborhoods: List<AssistancePreferredNeighborhoodEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceImovelCharacteristicCrossRefEntity::class,
    )
    val preferredImovelCharacteristics: List<AssistanceImovelCharacteristicCrossRefEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceInteractionEntity::class,
    )
    val interactions: List<AssistanceInteractionEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = ImovelOrm::class,
        associateBy = Junction(
            value = AssistanceSentImovelCrossRefEntity::class,
            parentColumn = "assistance_id",
            entityColumn = "property_id",
        ),
    )
    val imoveisEnviados: List<ImovelEnviadoProjection>,
)

data class ImovelEnviadoProjection(
    @Embedded val imovel: ImovelOrm,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = ImovelFileOrm::class,
    )
    val files: List<ImovelFileOrm>,
)
