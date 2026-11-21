package com.fstengineering.leasilycrm.clientes.presentation.mappers

import com.fstengineering.leasilycrm.clientes.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clientes.presentation.models.ClienteListItemUiState
import com.fstengineering.leasilycrm.core.presentation.models.ClienteUiState

fun ClienteReadModel.toClienteListItemUiState() = ClienteListItemUiState(
    cliente = ClienteUiState(
        id = id,
        name = name,
        city = city,
        hospedesCount = hospedesCount,
        hasUnfinishedAtendimento = hasUnfinishedAtendimento,
        hasUnfinishedStay = hasUnfinishedStay,
    ),
    currentStayId = currentStayId,
)
