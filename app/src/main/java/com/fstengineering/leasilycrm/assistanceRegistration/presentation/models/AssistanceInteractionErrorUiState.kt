package com.fstengineering.leasilycrm.assistanceRegistration.presentation.models

import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.fields.AssistanceInteractionField
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class AssistanceInteractionErrorUiState(
    val errors: PersistentMap<AssistanceInteractionField, Int?> = AssistanceInteractionField.entries
        .associateWith { null }
        .toPersistentMap(),
)
