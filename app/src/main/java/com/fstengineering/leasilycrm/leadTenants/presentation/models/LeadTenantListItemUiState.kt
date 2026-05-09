package com.fstengineering.leasilycrm.leadTenants.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState

data class LeadTenantListItemUiState(
    val leadTenant: LeadTenantUiState,
    val currentStayId: Long?,
)
