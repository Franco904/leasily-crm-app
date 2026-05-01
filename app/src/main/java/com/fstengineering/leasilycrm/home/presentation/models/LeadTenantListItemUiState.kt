package com.fstengineering.leasilycrm.home.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState

data class LeadTenantListItemUiState(
    val leadTenant: LeadTenantUiState,
    val currentStayId: Long?,
)
