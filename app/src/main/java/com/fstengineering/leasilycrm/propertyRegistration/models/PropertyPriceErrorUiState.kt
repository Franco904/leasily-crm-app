package com.fstengineering.leasilycrm.propertyRegistration.models

import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyPriceField
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class PropertyPriceErrorUiState(
    val errors: PersistentMap<PropertyPriceField, Int?> = PropertyPriceField.entries
        .associateWith { null }
        .toPersistentMap(),
)
