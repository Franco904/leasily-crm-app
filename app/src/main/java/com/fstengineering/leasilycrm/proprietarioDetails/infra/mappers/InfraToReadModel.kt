package com.fstengineering.leasilycrm.proprietarioDetails.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import kotlinx.collections.immutable.toPersistentList

fun ProprietarioCompound.toReadModel(): ProprietarioReadModel {
    return ProprietarioReadModel(
        name = proprietario.name,
        phone = proprietario.phone,
        email = proprietario.email,
        paymentKey = proprietario.paymentKey,
        files = files.map { it.toReadModel() }.toPersistentList(),
    )
}

fun ProprietarioFileOrm.toReadModel(): ProprietarioFileReadModel {
    return ProprietarioFileReadModel(
        uriString = proprietarioFileUri,
        name = proprietarioFileName,
    )
}
