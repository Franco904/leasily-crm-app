package com.fstengineering.leasilycrm.proprietarios.presentation.mappers

import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.proprietarios.application.models.ProprietarioReadModel

fun ProprietarioReadModel.toProprietarioUiState() = ProprietarioUiState(
    id = id,
    name = name,
)
