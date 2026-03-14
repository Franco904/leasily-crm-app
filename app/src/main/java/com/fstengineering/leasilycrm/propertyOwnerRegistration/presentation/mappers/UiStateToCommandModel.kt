package com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.mappers

import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.PropertyOwnerFileCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerDraftFormUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerFileDraftFormUiState
import kotlinx.collections.immutable.toPersistentList

fun PropertyOwnerDraftFormUiState.Loaded.toCommandModel(): PropertyOwnerCommandModel {
    return PropertyOwnerCommandModel(
        id = id,
        createDateTime = createDateTime,
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = files.map { it.toCommandModel() }.toPersistentList(),
    )
}

fun PropertyOwnerFileDraftFormUiState.toCommandModel(): PropertyOwnerFileCommandModel {
    return PropertyOwnerFileCommandModel(
        id = id,
        uriString = uri.toString(),
        name = name,
    )
}
