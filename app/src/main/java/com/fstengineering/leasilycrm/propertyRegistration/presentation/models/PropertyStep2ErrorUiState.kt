package com.fstengineering.leasilycrm.propertyRegistration.presentation.models

import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyStep2Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class PropertyStep2ErrorUiState(
    val errors: PersistentMap<PropertyStep2Field, Int?> = PropertyStep2Field.entries
        .associateWith { null }
        .toPersistentMap(),
) {
    val hasErrors = errors.any { it.value != null }
}
