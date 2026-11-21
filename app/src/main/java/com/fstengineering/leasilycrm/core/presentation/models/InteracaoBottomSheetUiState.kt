package com.fstengineering.leasilycrm.core.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

data class InteracaoBottomSheetUiState(
    val date: AppDate,
    val title: String,
    val description: String?,
)
