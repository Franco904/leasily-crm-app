package com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.dtos.PropertyOwnerFileReadModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.dtos.PropertyOwnerReadModel
import kotlinx.collections.immutable.toPersistentList

fun PropertyOwnerCompound.toReadModel(): PropertyOwnerReadModel {
    return PropertyOwnerReadModel(
        id = propertyOwner.id,
        createDateTime = propertyOwner.createDateTime,
        updateDateTime = propertyOwner.updateDateTime,
        name = propertyOwner.name,
        phone = propertyOwner.phone,
        email = propertyOwner.email,
        paymentKey = propertyOwner.paymentKey,
        files = files.map { it.toReadModel() }.toPersistentList(),
    )
}

private fun PropertyOwnerFileEntity.toReadModel() = PropertyOwnerFileReadModel(
    id = id,
    uriString = propertyOwnerFileUri,
    name = propertyOwnerFileName,
)
