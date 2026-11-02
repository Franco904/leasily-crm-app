package com.fstengineering.leasilycrm.imovelRegistration.presentation.models

import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.fields.ImovelStep2Field
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class ImovelStep2ErrorUiState(
    val errors: PersistentMap<ImovelStep2Field, Int?> = ImovelStep2Field.entries
        .associateWith { null }
        .toPersistentMap(),
) {
    val hasErrors = errors.any { it.value != null }
}
