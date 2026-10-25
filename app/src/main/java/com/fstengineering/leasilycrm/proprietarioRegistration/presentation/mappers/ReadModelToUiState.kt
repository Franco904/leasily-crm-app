package com.fstengineering.leasilycrm.proprietarioRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioFileUiState
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioReadModel.toUiState() = ProprietarioDraftFormUiState.Loaded(
    id = id,
    createDateTime = createDateTime,
    name = name,
    phone = phone,
    email = email,
    paymentKey = paymentKey,
    files = files.map { it.toUiState() }.toPersistentList(),
)

fun ProprietarioFileReadModel.toUiState(): ProprietarioFileUiState {
    return ProprietarioFileUiState(
        id = id,
        uri = uriString.toUri(),
        name = name,
    )
}
