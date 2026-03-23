package com.fstengineering.leasilycrm.core.presentation.models

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate

data class AssistanceInteractionBottomSheetUiState(
    val date: AppDate,
    val title: String,
    val description: String?,
)
