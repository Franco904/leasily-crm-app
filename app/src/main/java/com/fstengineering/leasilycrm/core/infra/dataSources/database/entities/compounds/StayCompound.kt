package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity

data class StayCompound(
    @Embedded val stay: StayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "stay_id",
        entity = StayFileEntity::class,
    )
    val files: List<StayFileEntity>,
)
