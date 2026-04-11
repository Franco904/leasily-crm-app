package com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

sealed interface LeadTenantDraftFormUiState {
    data object Loading : LeadTenantDraftFormUiState

    data class Loaded(
        val id: Long?,
        val createDateTime: AppDateTime?,
        val tag: String?,
        val name: String?,
        val city: String?,
        val identityDocument: String?,
        val phone: String?,
        val email: String?,
        val contactOrigin: String?,
        val observations: String?,
        val isLead: Boolean,
    ) : LeadTenantDraftFormUiState {
        val isAnyFormFieldInitialized: Boolean
            get() =
                tag != null || name != null || city != null || identityDocument != null || phone != null || email != null || contactOrigin != null || observations != null
    }
}
