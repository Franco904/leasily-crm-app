package com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class ProprietarioErrorUiState(
    val errors: PersistentMap<ProprietarioField, Int?> = ProprietarioField.entries
        .associateWith { null }
        .toPersistentMap(),
)
