package com.fstengineering.leasilycrm.proprietarios.infra.mappers

import com.fstengineering.leasilycrm.proprietarios.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarios.infra.projections.ProprietarioNameProjection

fun ProprietarioNameProjection.toReadModel(): ProprietarioReadModel =
    ProprietarioReadModel(id = id, name = name)
