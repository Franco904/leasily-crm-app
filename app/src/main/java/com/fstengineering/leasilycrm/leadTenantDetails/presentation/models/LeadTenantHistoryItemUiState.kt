package com.fstengineering.leasilycrm.leadTenantDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class LeadTenantHistoryItemUiState(
    val id: Long,
    val isAssistance: Boolean,
    val dateTime: AppDateTime,
)
