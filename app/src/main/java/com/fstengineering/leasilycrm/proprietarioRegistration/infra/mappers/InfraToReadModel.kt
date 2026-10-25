package com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioReadModel
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioCompound.toProprietarioReadModel(): ProprietarioReadModel {
    return ProprietarioReadModel(
        id = proprietario.id,
        createDateTime = proprietario.createDateTime,
        updateDateTime = proprietario.updateDateTime,
        name = proprietario.name,
        phone = proprietario.phone,
        email = proprietario.email,
        paymentKey = proprietario.paymentKey,
        files = files.map { it.toProprietarioFileReadModel() }.toPersistentList(),
    )
}

private fun ProprietarioFileOrm.toProprietarioFileReadModel() = ProprietarioFileReadModel(
    id = id,
    uriString = proprietarioFileUri,
    name = proprietarioFileName,
)
