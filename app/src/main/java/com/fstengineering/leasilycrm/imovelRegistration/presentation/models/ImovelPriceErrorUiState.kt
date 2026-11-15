package com.fstengineering.leasilycrm.imovelRegistration.presentation.models

import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.fields.ImovelPriceField
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class ImovelPriceErrorUiState(
    val errors: PersistentMap<ImovelPriceField, Int?> = ImovelPriceField.entries
        .associateWith { null }
        .toPersistentMap(),
) {
    val hasErrors = errors.any { it.value != null }
}
