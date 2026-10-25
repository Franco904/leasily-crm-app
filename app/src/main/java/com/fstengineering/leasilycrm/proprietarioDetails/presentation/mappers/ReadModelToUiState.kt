package com.fstengineering.leasilycrm.proprietarioDetails.presentation.mappers

import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.models.ProprietarioFileUiState
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.models.ProprietarioUiState
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioReadModel.toProprietarioUiState() = ProprietarioUiState.Loaded(
    name = name,
    phone = phone,
    email = email,
    paymentKey = paymentKey,
    files = files.map { it.toProprietarioFileUiState() }.toPersistentList(),
)

private fun ProprietarioFileReadModel.toProprietarioFileUiState() = ProprietarioFileUiState(
    uriString = uriString,
    name = name,
)
