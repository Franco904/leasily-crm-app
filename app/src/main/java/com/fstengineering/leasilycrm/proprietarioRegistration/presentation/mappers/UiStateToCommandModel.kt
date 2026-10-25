package com.fstengineering.leasilycrm.proprietarioRegistration.presentation.mappers

import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.commands.ProprietarioCommandModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.commands.ProprietarioFileCommandModel
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioFileUiState
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioDraftFormUiState.Loaded.toProprietarioCommandModel(): ProprietarioCommandModel {
    return ProprietarioCommandModel(
        id = id,
        createDateTime = createDateTime,
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = files.map { it.toProprietarioFileCommandModel() }.toPersistentList(),
    )
}

private fun ProprietarioFileUiState.toProprietarioFileCommandModel(): ProprietarioFileCommandModel {
    return ProprietarioFileCommandModel(
        id = id,
        uriString = uri.toString(),
        name = name,
    )
}
