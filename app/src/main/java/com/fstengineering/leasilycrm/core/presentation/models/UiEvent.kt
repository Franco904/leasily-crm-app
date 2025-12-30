package com.fstengineering.leasilycrm.core.presentation.models

sealed interface UiEvent {
    data class ShowSnackBar(val message: Int) : UiEvent
    data class ShowActionSnackBar(
        val message: Int,
        val actionLabel: Int,
        val action: () -> Unit,
    ) : UiEvent

    data class ShowErrorBottomSheet(val error: ErrorBottomSheetData) : UiEvent
}
