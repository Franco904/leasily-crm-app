package com.fstengineering.leasilycrm.core.presentation.models

import android.content.Context

data class ErrorBottomSheetUiState(
    val title: Int,
    val description: Int,
    val secondaryActionLabel: Int?,
    val onSecondaryAction: (Context.(onErrorEvent: (UiEvent) -> Unit) -> Unit)?,
)
