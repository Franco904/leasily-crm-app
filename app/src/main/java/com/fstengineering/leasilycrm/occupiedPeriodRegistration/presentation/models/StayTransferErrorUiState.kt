package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class StayTransferErrorUiState(
    val errors: PersistentMap<StayTransferField, Int?> = StayTransferField.entries
        .associateWith { null }
        .toPersistentMap(),
)
