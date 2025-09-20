package com.fstengineering.leasilycrm.assistanceRegistration.models

import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceStep1Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class AssistanceInteractionErrorUiState(
    val errors: PersistentMap<AssistanceStep1Field, Int?> = AssistanceStep1Field.entries
        .associateWith { null }
        .toPersistentMap(),
)
