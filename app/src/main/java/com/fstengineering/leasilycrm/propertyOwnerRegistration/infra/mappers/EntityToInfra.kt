package com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding
import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwnerFile

fun PropertyOwner.toLocalDataModel(): PropertyOwnerEntity {
    return PropertyOwnerEntity(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        name = name.value.normalizeEncoding(),
        phone = phone?.value,
        email = email?.value,
        paymentKey = paymentKey?.value,
        isDeleted = false,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}

fun PropertyOwnerFile.toLocalDataModel(
    propertyOwnerId: Long,
): PropertyOwnerFileEntity {
    return PropertyOwnerFileEntity(
        id = id,
        propertyOwnerFileUri = uriString,
        propertyOwnerFileName = name,
        propertyOwnerId = propertyOwnerId,
    )
}
