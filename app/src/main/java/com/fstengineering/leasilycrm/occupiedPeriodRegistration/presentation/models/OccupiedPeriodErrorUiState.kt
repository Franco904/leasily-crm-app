package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class OccupiedPeriodErrorUiState(
    val errors: PersistentMap<OccupiedPeriodField, Int?> = OccupiedPeriodField.entries
        .associateWith { null }
        .toPersistentMap(),
)
