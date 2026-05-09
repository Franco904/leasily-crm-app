package com.fstengineering.leasilycrm.core.presentation.models

data class LeadTenantUiState(
    val id: Long,
    val name: String,
    val city: String?,
    val guestsCount: Int? = null,
    val hasUnfinishedAssistance: Boolean = false,
    val hasUnfinishedStay: Boolean = false,
)
