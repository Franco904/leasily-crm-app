package com.fstengineering.leasilycrm.assistanceRegistration.presentation.models

import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.fields.AssistanceStep1Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class AssistanceStep1ErrorUiState(
    val errors: PersistentMap<AssistanceStep1Field, Int?> = AssistanceStep1Field.entries
        .associateWith { null }
        .toPersistentMap(),
)
