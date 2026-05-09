package com.fstengineering.leasilycrm.propertyRegistration.presentation.models

import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyStep1Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class PropertyStep1ErrorUiState(
    val errors: PersistentMap<PropertyStep1Field, Int?> = PropertyStep1Field.entries
        .associateWith { null }
        .toPersistentMap(),
) {
    val hasErrors = errors.any { it.value != null }
}
