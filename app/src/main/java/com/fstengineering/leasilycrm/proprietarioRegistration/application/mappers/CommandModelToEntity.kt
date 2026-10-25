package com.fstengineering.leasilycrm.proprietarioRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.commands.ProprietarioCommandModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.commands.ProprietarioFileCommandModel
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.ProprietarioFile
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioCommandModel.toProprietario(): AppResult<Proprietario, ValidationErrors> {
    val errors = mutableListOf<DomainError>()

    val parsed = Proprietario.parse(
        id = id,
        createDateTime = createDateTime,
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = files.map { it.toProprietarioFile() }.toPersistentList(),
    )
        .fold(onError = { errors.addAll(0, it.errors); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

    return AppResult.Success(data = requireNotNull(parsed))
}

private fun ProprietarioFileCommandModel.toProprietarioFile() = ProprietarioFile(
    id = id,
    uriString = uriString,
    name = name,
)
