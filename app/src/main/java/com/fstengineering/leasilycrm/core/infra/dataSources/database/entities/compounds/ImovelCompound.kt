package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm

data class ImovelCompound(
    @Embedded val imovel: ImovelOrm,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id",
        entity = ImovelCharacteristicCrossRefOrm::class
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
