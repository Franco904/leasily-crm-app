package com.fstengineering.leasilycrm.clienteDetails.infra.projections

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoImovelEnviadoCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoPreferredNeighborhoodOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.InteracaoOrm
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
        entity = AtendimentoOrm::class,
    )
    val atendimentos: List<AtendimentoProjection>,
    @Relation(
        parentColumn = "id",
        entityColumn = "tenant_id",
        entity = StayEntity::class,
        projection = ["id", "check_in_date_time", "status", "is_deleted"],
    )
    val estadias: List<EstadiaProjection>,
)

data class AtendimentoProjection(
    @Embedded val atendimento: AtendimentoOrm,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AtendimentoPreferredNeighborhoodOrm::class,
    )
    val preferredNeighborhoods: List<AtendimentoPreferredNeighborhoodOrm>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AtendimentoImovelCharacteristicCrossRefOrm::class,
    )
    val preferredImovelCharacteristics: List<AtendimentoImovelCharacteristicCrossRefOrm>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = InteracaoOrm::class,
    )
    val interacoes: List<InteracaoOrm>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = ImovelOrm::class,
        associateBy = Junction(
            value = AtendimentoImovelEnviadoCrossRefOrm::class,
            parentColumn = "assistance_id",
            entityColumn = "property_id",
        ),
        projection = ["id", "property_type_id", "neighborhood", "street_address", "number", "is_deleted"],
    )
    val imoveisEnviados: List<ImovelEnviadoProjection>,
)

data class ImovelEnviadoProjection(
    val id: Long,
    @ColumnInfo(name = "property_type_id") val typeId: Long,
    val neighborhood: String,
    @ColumnInfo(name = "street_address") val streetAddress: String,
    val number: Int?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = ImovelFileOrm::class,
        projection = ["property_file_uri", "is_main_picture"],
    )
    val files: List<ImovelFileProjection>,
)

data class ImovelFileProjection(
    @ColumnInfo(name = "property_file_uri") val uriString: String,
    @ColumnInfo(name = "is_main_picture") val isMainPicture: Boolean,
)

data class EstadiaProjection(
    val id: Long,
    @ColumnInfo(name = "check_in_date_time") val checkInDateTime: AppDateTime,
    val status: Int?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
)
