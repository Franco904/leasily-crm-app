package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceImovelCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ClienteOrm

data class AssistanceCompound(
    @Embedded val assistance: AssistanceEntity,
    @Relation(
        parentColumn = "lead_tenant_id",
        entityColumn = "id",
        entity = ClienteOrm::class,
    )
    val cliente: ClienteOrm,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistancePreferredNeighborhoodEntity::class,
    )
    val preferredNeighborhoods: List<AssistancePreferredNeighborhoodEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceImovelCharacteristicCrossRefEntity::class
    )
    val preferredImovelCharacteristics: List<AssistanceImovelCharacteristicCrossRefEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceInteractionEntity::class,
    )
    val interactions: List<AssistanceInteractionEntity>,
)
