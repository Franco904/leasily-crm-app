package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.presentation.models.InteracaoBottomSheetUiState
import com.fstengineering.leasilycrm.core.presentation.models.InteracaoCardUiState

sealed interface InteracaoDraftFormUiState {
    data object Loading : InteracaoDraftFormUiState

    data class Loaded(
        val id: Long?,
        val date: AppDate,
        val title: String,
        val description: String?,
    ) : InteracaoDraftFormUiState {
        fun toCardUiState() = InteracaoCardUiState(
            date = date,
            title = title,
        )

        fun toBottomSheetUiState() = InteracaoBottomSheetUiState(
            date = date,
            title = title,
            description = description,
        )
    }
}
