package com.fstengineering.leasilycrm.clienteRegistration.presentation.models

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class ClienteErrorUiState(
    val errors: PersistentMap<ClienteField, Int?> = ClienteField.entries
        .associateWith { null }
        .toPersistentMap(),
)
