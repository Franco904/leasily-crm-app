package com.fstengineering.leasilycrm.leadTenantRegistration.models

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class LeadTenantErrorUiState(
    val errors: PersistentMap<LeadTenantField, Int?> = LeadTenantField.entries
        .associateWith { null }
        .toPersistentMap(),
)
