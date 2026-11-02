package com.fstengineering.leasilycrm.proprietarioRegistration.presentation.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.ProprietarioFile
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioDraftFormUiState.Loaded.toProprietario(): AppResult<Proprietario, ValidationErrors> {
    return Proprietario.parse(
        id = id,
        createDateTime = createDateTime,
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = files
            .map { ProprietarioFile(id = it.id, uriString = it.uriString, name = it.name) }
            .toPersistentList(),
    )
}
