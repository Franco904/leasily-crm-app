package com.fstengineering.leasilycrm.customerSegments.models

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

data class CustomerSegmentErrorUiState(
    val errors: PersistentMap<CustomerSegmentField, Int?> = CustomerSegmentField.entries
        .associateWith { null }
        .toPersistentMap(),
)
