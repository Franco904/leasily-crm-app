package com.fstengineering.leasilycrm.atendimentoRegistration.infra.projections

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoImovelEnviadoCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoPreferredNeighborhoodOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ClienteOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.InteracaoOrm

/**
 * Atendimento a editar, com Cliente, Preferências de procura, Interações e Imóveis enviados lidos
 * numa única transação. As relações trazem também os excluídos; o descarte é do mapper.
 */
data class AtendimentoRegistrationProjection(
    @Embedded val atendimento: AtendimentoOrm,
    @Relation(
        parentColumn = "lead_tenant_id",
        entityColumn = "id",
        entity = ClienteOrm::class,
        projection = ["name"],
    )
    val clienteName: String,
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
        projection = [
            "id", "property_type_id", "neighborhood", "street_address", "number",
            "property_sea_proximity_type_id", "distance_to_sea", "bedrooms", "is_deleted",
        ],
    )
    val imoveisEnviados: List<ImovelEnviadoProjection>,
)

data class ImovelEnviadoProjection(
    val id: Long,
    @ColumnInfo(name = "property_type_id") val typeId: Long,
    val neighborhood: String,
    @ColumnInfo(name = "street_address") val streetAddress: String,
    val number: Int?,
    @ColumnInfo(name = "property_sea_proximity_type_id") val seaProximityTypeId: Long?,
    @ColumnInfo(name = "distance_to_sea") val distanceToSea: AppDecimal?,
    val bedrooms: Int,
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
