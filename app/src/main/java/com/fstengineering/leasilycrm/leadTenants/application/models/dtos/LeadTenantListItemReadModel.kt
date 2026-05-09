package com.fstengineering.leasilycrm.leadTenants.application.models.dtos

data class LeadTenantListItemReadModel(
    val id: Long,
    val name: String,
    val city: String?,
    val hasUnfinishedAssistance: Boolean,
    val guestsCount: Int?,
    val hasUnfinishedStay: Boolean,
    val currentStayId: Long?,
)
