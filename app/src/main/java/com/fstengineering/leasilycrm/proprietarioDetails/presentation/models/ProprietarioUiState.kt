package com.fstengineering.leasilycrm.proprietarioDetails.presentation.models

import kotlinx.collections.immutable.ImmutableList

sealed interface ProprietarioUiState {
    data object Loading : ProprietarioUiState

    data class Loaded(
        val name: String?,
        val phone: String?,
        val email: String?,
        val paymentKey: String?,
        val files: ImmutableList<ProprietarioFileUiState>,
    ) : ProprietarioUiState
}
