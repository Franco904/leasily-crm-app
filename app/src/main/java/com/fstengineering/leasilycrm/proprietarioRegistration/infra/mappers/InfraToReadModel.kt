package com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioReadModel
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioCompound.toReadModel(): ProprietarioReadModel {
    return ProprietarioReadModel(
        id = proprietario.id,
        createDateTime = proprietario.createDateTime,
        updateDateTime = proprietario.updateDateTime,
        name = proprietario.name,
        phone = proprietario.phone,
        email = proprietario.email,
        paymentKey = proprietario.paymentKey,
        files = files.map { it.toReadModel() }.toPersistentList(),
    )
}

private fun ProprietarioFileOrm.toReadModel() = ProprietarioFileReadModel(
    id = id,
    uriString = proprietarioFileUri,
    name = proprietarioFileName,
)
