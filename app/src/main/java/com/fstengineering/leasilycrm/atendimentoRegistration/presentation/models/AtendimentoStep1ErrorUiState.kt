package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models

import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.fields.AtendimentoStep1Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class AtendimentoStep1ErrorUiState(
    val errors: PersistentMap<AtendimentoStep1Field, Int?> = AtendimentoStep1Field.entries
        .associateWith { null }
        .toPersistentMap(),
)
