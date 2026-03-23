package com.fstengineering.leasilycrm.propertyRegistration.presentation.models

import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyPriceField
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class PropertyPriceErrorUiState(
    val errors: PersistentMap<PropertyPriceField, Int?> = PropertyPriceField.entries
        .associateWith { null }
        .toPersistentMap(),
) {
    val hasErrors = errors.any { it.value != null }
}
