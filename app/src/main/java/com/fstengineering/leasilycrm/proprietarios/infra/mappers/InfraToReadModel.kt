package com.fstengineering.leasilycrm.proprietarios.infra.mappers

import com.fstengineering.leasilycrm.proprietarios.infra.projections.ProprietarioNameProjection
import com.fstengineering.leasilycrm.proprietarios.application.models.ProprietarioReadModel

fun ProprietarioNameProjection.toProprietarioReadModel(): ProprietarioReadModel =
    ProprietarioReadModel(id = id, name = name)
