package com.fstengineering.leasilycrm.stayRegistration.models

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class StayErrorUiState(
    val errors: PersistentMap<StayField, Int?> = StayField.entries
        .associateWith { null }
        .toPersistentMap(),
)
