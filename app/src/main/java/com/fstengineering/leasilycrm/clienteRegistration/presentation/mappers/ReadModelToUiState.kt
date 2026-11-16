package com.fstengineering.leasilycrm.clienteRegistration.presentation.mappers

import com.fstengineering.leasilycrm.clienteRegistration.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteDraftFormUiState

fun ClienteReadModel.toClienteDraftFormUiState() = ClienteDraftFormUiState.Loaded(
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
    isLead = isLead,
)
