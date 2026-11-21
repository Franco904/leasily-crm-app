package com.fstengineering.leasilycrm.clienteRegistration.presentation.mappers

import com.fstengineering.leasilycrm.clienteRegistration.domain.entities.Cliente
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors

fun ClienteDraftFormUiState.Loaded.toCliente(): AppResult<Cliente, ValidationErrors> {
    return Cliente.parse(
        id = id,
        createDateTime = createDateTime,
        tag = tag,
        name = name,
        city = city,
        identityDocument = identityDocument,
        phone = phone,
        email = email,
        contactOrigin = contactOrigin,
        observations = observations,
    )
}
