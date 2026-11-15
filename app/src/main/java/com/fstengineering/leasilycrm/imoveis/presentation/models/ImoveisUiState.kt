package com.fstengineering.leasilycrm.imoveis.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.ImmutableList

sealed interface ImoveisUiState {
    data object Loading : ImoveisUiState
    data class Loaded(val imoveis: ImmutableList<ImovelCardUiState>) : ImoveisUiState
}
