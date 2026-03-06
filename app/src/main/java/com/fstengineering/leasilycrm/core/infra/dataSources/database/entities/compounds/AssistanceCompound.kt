package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity

data class AssistanceCompound(
    @Embedded val assistance: AssistanceEntity,
    @Relation(
        parentColumn = "tenant_id",
        entityColumn = "id",
        entity = TenantEntity::class,
    )
    val tenant: TenantEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistancePreferredNeighborhoodEntity::class,
    )
    val preferredNeighborhoods: List<AssistancePreferredNeighborhoodEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistancePropertyCharacteristicCrossRefEntity::class
    )
    val preferredPropertyCharacteristics: List<AssistancePropertyCharacteristicCrossRefEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "assistance_id",
        entity = AssistanceInteractionEntity::class,
    )
    val interactions: List<AssistanceInteractionEntity>,
)
