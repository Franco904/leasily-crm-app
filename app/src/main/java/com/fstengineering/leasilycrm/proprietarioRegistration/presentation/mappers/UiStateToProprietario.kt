package com.fstengineering.leasilycrm.proprietarioRegistration.presentation.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.ProprietarioFile
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioDraftFormUiState.Loaded.toProprietario(): AppResult<Proprietario> {
    val fileErrors = mutableListOf<DomainError>()

    val parsedFiles = files
        .mapNotNull {
            ProprietarioFile.parse(id = it.id, uriString = it.uriString, name = it.name)
                .fold(
                    onError = { error -> fileErrors.addAll(error.toDomainErrors()); null },
                    onSuccess = { file -> file })
        }
        .toPersistentList()

    val proprietario = Proprietario.parse(
        id = id,
        createDateTime = createDateTime,
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = parsedFiles,
    )

    if (fileErrors.isEmpty()) return proprietario

    val errors =
        fileErrors + ((proprietario as? AppResult.Error)?.error?.toDomainErrors() ?: emptyList())
    return AppResult.Error(ValidationErrors(errors.toPersistentList()))
}
