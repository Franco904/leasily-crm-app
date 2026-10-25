package com.fstengineering.leasilycrm.proprietarios.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import kotlinx.collections.immutable.ImmutableList

sealed interface ProprietariosUiState {
    data object Loading : ProprietariosUiState
    data class Loaded(val proprietarios: ImmutableList<ProprietarioUiState>) : ProprietariosUiState
}
