package com.fstengineering.leasilycrm.leadTenantDetails.presentation.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

sealed interface LeadTenantDetailsUiState {
    data object Loading : LeadTenantDetailsUiState

    data class Loaded(
        val id: Long,
        val tag: String?,
        val name: String,
        val city: String?,
        val identityDocument: String?,
        val phone: String?,
        val email: String?,
        val contactOrigin: String?,
        val observations: String?,
        val isLead: Boolean,
        val isOld: Boolean,
        val currentStayId: Long?,
        val currentAssistance: AssistanceUiState?,
        val pastAssistances: ImmutableList<AssistanceUiState>,
        val historyByYear: ImmutableMap<Int, ImmutableList<LeadTenantHistoryItemUiState>>,
    ) : LeadTenantDetailsUiState
}
