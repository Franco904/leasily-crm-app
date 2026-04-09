package com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.mappers

import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerFileCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerDraftFormUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerFileUiState
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

fun PropertyOwnerFileUiState.toCommandModel(): PropertyOwnerFileCommandModel {
    return PropertyOwnerFileCommandModel(
        id = id,
        uriString = uri.toString(),
        name = name,
    )
}
