package com.fstengineering.leasilycrm.assistanceRegistration.models

import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceNeighborhoodField
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class AssistanceSearchedNeighborhoodErrorUiState(
    val errors: PersistentMap<AssistanceNeighborhoodField, Int?> = AssistanceNeighborhoodField.entries
        .associateWith { null }
        .toPersistentMap(),
)
