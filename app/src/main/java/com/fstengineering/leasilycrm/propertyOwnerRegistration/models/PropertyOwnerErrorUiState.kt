package com.fstengineering.leasilycrm.propertyOwnerRegistration.models

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class PropertyOwnerErrorUiState(
    val errors: PersistentMap<PropertyOwnerField, Int?> = PropertyOwnerField.entries
        .associateWith { null }
        .toPersistentMap(),
)
