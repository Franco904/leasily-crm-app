package com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.PropertyOwnerFileReadModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerDraftFormUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerFileDraftFormUiState
import kotlinx.collections.immutable.toPersistentList

fun PropertyOwnerReadModel.toDraftForm() = PropertyOwnerDraftFormUiState.Loaded(
    id = id,
    createDateTime = createDateTime,
    name = name,
    phone = phone,
    email = email,
    paymentKey = paymentKey,
    files = files.map { it.toFileDraftForm() }.toPersistentList(),
)

fun PropertyOwnerFileReadModel.toFileDraftForm(): PropertyOwnerFileDraftFormUiState {
    return PropertyOwnerFileDraftFormUiState(
        id = id,
        uri = uriString.toUri(),
        name = name,
    )
}
