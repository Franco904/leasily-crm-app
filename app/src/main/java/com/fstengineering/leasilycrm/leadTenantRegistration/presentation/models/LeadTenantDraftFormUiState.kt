package com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

sealed interface LeadTenantDraftFormUiState {
    data object Loading : LeadTenantDraftFormUiState

    data class Loaded(
        val id: Long? = null,
        val createDateTime: AppDateTime? = null,
        val tag: String? = null,
        val name: String? = null,
        val city: String? = null,
        val identityDocument: String? = null,
        val phone: String? = null,
        val email: String? = null,
        val contactOrigin: String? = null,
        val observations: String? = null,
        val isLead: Boolean,
    ) : LeadTenantDraftFormUiState {
        val isAnyFormFieldInitialized: Boolean
            get() =
                tag != null || name != null || city != null || identityDocument != null || phone != null || email != null || contactOrigin != null || observations != null

        companion object {
            val Empty = Loaded(isLead = true)
        }
    }
}
