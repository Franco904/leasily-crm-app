package com.fstengineering.leasilycrm.clienteDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class ClienteHistoryItemUiState(
    val id: Long,
    val isAssistance: Boolean,
    val dateTime: AppDateTime,
)
