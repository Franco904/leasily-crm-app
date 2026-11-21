package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models

import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.fields.InteracaoField
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class InteracaoErrorUiState(
    val errors: PersistentMap<InteracaoField, Int?> = InteracaoField.entries
        .associateWith { null }
        .toPersistentMap(),
)
