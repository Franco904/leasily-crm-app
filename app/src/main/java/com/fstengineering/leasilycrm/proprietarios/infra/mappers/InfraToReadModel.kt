package com.fstengineering.leasilycrm.proprietarios.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.proprietarios.application.models.ProprietarioReadModel

fun ProprietarioOrm.toProprietarioReadModel(): ProprietarioReadModel =
    ProprietarioReadModel(id = requireNotNull(id), name = name)
