package com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.dtos.PropertyOwnerFileReadModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.dtos.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerDraftFormUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerFileUiState
import kotlinx.collections.immutable.toPersistentList

fun PropertyOwnerReadModel.toUiState() = PropertyOwnerDraftFormUiState.Loaded(
    id = id,
    createDateTime = createDateTime,
    name = name,
    phone = phone,
    email = email,
    paymentKey = paymentKey,
    files = files.map { it.toUiState() }.toPersistentList(),
)

fun PropertyOwnerFileReadModel.toUiState(): PropertyOwnerFileUiState {
    return PropertyOwnerFileUiState(
        id = id,
        uri = uriString.toUri(),
        name = name,
    )
}
