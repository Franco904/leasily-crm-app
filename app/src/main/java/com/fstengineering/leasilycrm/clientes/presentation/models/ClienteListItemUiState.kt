package com.fstengineering.leasilycrm.clientes.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.ClienteUiState

data class ClienteListItemUiState(
    val cliente: ClienteUiState,
    val currentStayId: Long?,
)
