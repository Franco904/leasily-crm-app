package com.fstengineering.leasilycrm.propertyRegistration.models

import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep2Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class PropertyStep2ErrorUiState(
    val errors: PersistentMap<PropertyStep2Field, Int?> = PropertyStep2Field.entries
        .associateWith { null }
        .toPersistentMap(),
)
