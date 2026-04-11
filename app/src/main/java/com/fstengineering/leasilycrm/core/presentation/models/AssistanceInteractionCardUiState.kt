package com.fstengineering.leasilycrm.core.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

data class AssistanceInteractionCardUiState(
    val date: AppDate,
    val title: String,
)
