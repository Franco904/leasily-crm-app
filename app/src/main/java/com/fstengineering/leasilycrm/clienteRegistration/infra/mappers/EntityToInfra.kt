package com.fstengineering.leasilycrm.clienteRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.clienteRegistration.domain.entities.Cliente
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ClienteOrm
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding

fun Cliente.toClienteOrm(): ClienteOrm {
    return ClienteOrm(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        tag = tag,
        name = name.value.normalizeEncoding(),
        city = city,
        identityDocument = identityDocument,
        phone = phone?.value,
        email = email?.value,
        contactOrigin = contactOrigin,
        observations = observations,
        isDeleted = false,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}
