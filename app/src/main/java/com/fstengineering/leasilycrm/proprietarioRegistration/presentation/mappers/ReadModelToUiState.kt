package com.fstengineering.leasilycrm.proprietarioRegistration.presentation.mappers

import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioFileUiState
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioReadModel.toProprietarioDraftFormUiState() = ProprietarioDraftFormUiState.Loaded(
    id = id,
    createDateTime = createDateTime,
    name = name,
    phone = phone,
    email = email,
    paymentKey = paymentKey,
    files = files.map { it.toProprietarioFileUiState() }.toPersistentList(),
)

private fun ProprietarioFileReadModel.toProprietarioFileUiState(): ProprietarioFileUiState {
    return ProprietarioFileUiState(
        id = id,
        uriString = uriString,
        name = name,
    )
}
