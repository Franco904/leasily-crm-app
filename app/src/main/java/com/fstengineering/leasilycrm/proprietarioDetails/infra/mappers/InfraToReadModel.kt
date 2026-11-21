package com.fstengineering.leasilycrm.proprietarioDetails.infra.mappers

import com.fstengineering.leasilycrm.proprietarioDetails.infra.projections.ProprietarioFileDetailsProjection
import com.fstengineering.leasilycrm.proprietarioDetails.infra.projections.ProprietarioDetailsProjection
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioDetailsProjection.toReadModel(): ProprietarioReadModel {
    return ProprietarioReadModel(
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = files.map { it.toReadModel() }.toPersistentList(),
    )
}

private fun ProprietarioFileDetailsProjection.toReadModel(): ProprietarioFileReadModel {
    return ProprietarioFileReadModel(
        uriString = uriString,
        name = name,
    )
}
