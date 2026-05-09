package com.fstengineering.leasilycrm.propertyOwnerDetails.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.propertyOwnerDetails.application.models.PropertyOwnerFileReadModel
import com.fstengineering.leasilycrm.propertyOwnerDetails.application.models.PropertyOwnerReadModel
import kotlinx.collections.immutable.toPersistentList

fun PropertyOwnerCompound.toReadModel(): PropertyOwnerReadModel {
    return PropertyOwnerReadModel(
        name = propertyOwner.name,
        phone = propertyOwner.phone,
        email = propertyOwner.email,
        paymentKey = propertyOwner.paymentKey,
        files = files.map { it.toReadModel() }.toPersistentList(),
    )
}

fun PropertyOwnerFileEntity.toReadModel(): PropertyOwnerFileReadModel {
    return PropertyOwnerFileReadModel(
        uriString = propertyOwnerFileUri,
        name = propertyOwnerFileName,
    )
}
