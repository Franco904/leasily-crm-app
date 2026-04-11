package com.fstengineering.leasilycrm.properties.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import com.fstengineering.leasilycrm.properties.application.models.dtos.PropertyReadModel

fun PropertyReadModel.toUiState(): PropertyCardUiState {
    val numberText = if (number != null) ", $number" else ""
    val streetAddressAndNumber = "${streetAddress}$numberText"

    return PropertyCardUiState(
        id = id,
        mainPictureUri = mainPictureUriString?.toUri(),
        neighborhood = neighborhood,
        streetAddressAndNumber = streetAddressAndNumber,
        type = type,
    )
}
