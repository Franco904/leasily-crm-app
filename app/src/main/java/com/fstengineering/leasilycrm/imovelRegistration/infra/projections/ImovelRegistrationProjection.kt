package com.fstengineering.leasilycrm.imovelRegistration.infra.projections

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm

/**
 * Imóvel a editar, com Proprietário, características, Preços e arquivos lidos numa única transação.
 * As relações trazem também os excluídos; a decisão sobre o Proprietário excluído é do DataSource.
 */
data class ImovelRegistrationProjection(
    @Embedded val imovel: ImovelOrm,
    @Relation(
        parentColumn = "property_owner_id",
        entityColumn = "id",
        entity = ProprietarioOrm::class,
        projection = ["id", "name", "is_deleted"],
    )
    val proprietario: ImovelProprietarioProjection?,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = ImovelCharacteristicCrossRefOrm::class,
    )
    val characteristics: List<ImovelCharacteristicCrossRefOrm>,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = ImovelPriceOrm::class,
    )
    val prices: List<ImovelPriceOrm>,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = ImovelFileOrm::class,
    )
    val files: List<ImovelFileOrm>,
)

data class ImovelProprietarioProjection(
    val id: Long,
    val name: String,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
)
