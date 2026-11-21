package com.fstengineering.leasilycrm.clienteRegistration.infra.mappers

import com.fstengineering.leasilycrm.clienteRegistration.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteRegistration.infra.projections.ClienteRegistrationProjection
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus

fun ClienteRegistrationProjection.toClienteReadModel() = ClienteReadModel(
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
    isLead = status == ClienteStatus.Lead.id,
)
