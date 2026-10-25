package com.fstengineering.leasilycrm.proprietarioDetails.infra.mappers

import com.fstengineering.leasilycrm.proprietarioDetails.infra.projections.ProprietarioFileDetailsProjection
import com.fstengineering.leasilycrm.proprietarioDetails.infra.projections.ProprietarioDetailsProjection
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioDetailsProjection.toProprietarioReadModel(): ProprietarioReadModel {
    return ProprietarioReadModel(
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = files.map { it.toProprietarioFileReadModel() }.toPersistentList(),
    )
}

private fun ProprietarioFileDetailsProjection.toProprietarioFileReadModel(): ProprietarioFileReadModel {
    return ProprietarioFileReadModel(
        uriString = uriString,
        name = name,
    )
}
