package com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers

import com.fstengineering.leasilycrm.proprietarioRegistration.infra.projections.ProprietarioFileRegistrationProjection
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.projections.ProprietarioRegistrationProjection
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioReadModel
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioRegistrationProjection.toProprietarioReadModel(): ProprietarioReadModel {
    return ProprietarioReadModel(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = files.map { it.toProprietarioFileReadModel() }.toPersistentList(),
    )
}

private fun ProprietarioFileRegistrationProjection.toProprietarioFileReadModel() = ProprietarioFileReadModel(
    id = id,
    uriString = uriString,
    name = name,
)
