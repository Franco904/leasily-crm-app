package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm

data class ProprietarioCompound(
    @Embedded val proprietario: ProprietarioOrm,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_owner_id",
        entity = ProprietarioFileOrm::class,
    )
    val files: List<ProprietarioFileOrm>,
)
