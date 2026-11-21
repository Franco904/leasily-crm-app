package com.fstengineering.leasilycrm.clienteDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.presentation.models.InteracaoCardUiState

data class InteracaoUiState(
    val id: Long,
    val date: AppDate,
    val title: String,
    val description: String?,
) {
    fun toCardUiState() = InteracaoCardUiState(
        date = date,
        title = title,
    )
}
