package com.fstengineering.leasilycrm.assistanceRegistration.models

import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceStep1Field
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep1Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class AssistanceStep1ErrorUiState(
    val errors: PersistentMap<AssistanceStep1Field, Int?> = AssistanceStep1Field.entries
        .associateWith { null }
        .toPersistentMap(),
)
