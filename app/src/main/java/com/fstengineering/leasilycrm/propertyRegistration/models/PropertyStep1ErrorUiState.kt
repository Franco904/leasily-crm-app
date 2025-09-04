package com.fstengineering.leasilycrm.propertyRegistration.models

import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep1Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class PropertyStep1ErrorUiState(
    val errors: PersistentMap<PropertyStep1Field, Int?> = PropertyStep1Field.entries
        .associateWith { null }
        .toPersistentMap(),
)
