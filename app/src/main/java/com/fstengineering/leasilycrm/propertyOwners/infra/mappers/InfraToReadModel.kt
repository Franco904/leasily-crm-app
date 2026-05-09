package com.fstengineering.leasilycrm.propertyOwners.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.propertyOwners.application.models.dtos.PropertyOwnerReadModel

fun PropertyOwnerCompound.toReadModel(): PropertyOwnerReadModel {
    return PropertyOwnerReadModel(
        id = requireNotNull(propertyOwner.id),
        name = propertyOwner.name,
    )
}
