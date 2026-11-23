package com.fstengineering.leasilycrm.imoveis.presentation.mappers

import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import com.fstengineering.leasilycrm.imoveis.application.models.dtos.ImovelReadModel

fun ImovelReadModel.toUiState(): ImovelCardUiState {
    val numberText = if (number != null) ", $number" else ""
    val streetAddressAndNumber = "${streetAddress}$numberText"

    return ImovelCardUiState(
        id = id,
        mainPictureUriString = mainPictureUriString,
        neighborhood = neighborhood,
        streetAddressAndNumber = streetAddressAndNumber,
        type = type,
    )
}
