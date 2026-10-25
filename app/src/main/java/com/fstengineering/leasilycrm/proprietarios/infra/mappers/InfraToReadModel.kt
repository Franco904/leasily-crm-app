package com.fstengineering.leasilycrm.proprietarios.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import com.fstengineering.leasilycrm.proprietarios.application.models.dtos.ProprietarioReadModel

fun ProprietarioCompound.toReadModel(): ProprietarioReadModel {
    return ProprietarioReadModel(
        id = requireNotNull(proprietario.id),
        name = proprietario.name,
    )
}

fun ProprietarioOrm.toProprietarioReadModel(): ProprietarioReadModel =
    ProprietarioReadModel(id = requireNotNull(id), name = name)
