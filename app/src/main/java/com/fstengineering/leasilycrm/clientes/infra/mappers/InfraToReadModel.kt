package com.fstengineering.leasilycrm.clientes.infra.mappers

import com.fstengineering.leasilycrm.clientes.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clientes.infra.projections.ClienteListItemProjection

fun ClienteListItemProjection.toReadModel() = ClienteReadModel(
    id = id,
    name = name,
    city = city,
    hasUnfinishedAtendimento = hasUnfinishedAtendimento,
    hospedesCount = hospedesCount,
    hasUnfinishedStay = hasUnfinishedStay,
    currentStayId = currentStayId,
)
