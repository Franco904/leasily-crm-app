package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class PropertyOwnerCompound(
    @Embedded val propertyOwner: PropertyOwnerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_owner_id",
        entity = PropertyOwnerFileEntity::class,
    )
    val files: List<PropertyOwnerFileEntity>,
)
