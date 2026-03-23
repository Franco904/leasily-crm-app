package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity

data class PropertyOwnerCompound(
    @Embedded val propertyOwner: PropertyOwnerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_owner_id",
        entity = PropertyOwnerFileEntity::class,
    )
    val files: List<PropertyOwnerFileEntity>,
)
