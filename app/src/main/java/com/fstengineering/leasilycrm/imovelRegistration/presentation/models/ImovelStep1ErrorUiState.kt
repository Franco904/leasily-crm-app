package com.fstengineering.leasilycrm.imovelRegistration.presentation.models

import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.fields.ImovelStep1Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class ImovelStep1ErrorUiState(
    val errors: PersistentMap<ImovelStep1Field, Int?> = ImovelStep1Field.entries
        .associateWith { null }
        .toPersistentMap(),
) {
    val hasErrors = errors.any { it.value != null }
}
