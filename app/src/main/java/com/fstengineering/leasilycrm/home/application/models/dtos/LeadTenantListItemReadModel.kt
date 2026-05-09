package com.fstengineering.leasilycrm.home.application.models.dtos

data class LeadTenantListItemReadModel(
    val id: Long,
    val name: String,
    val city: String?,
    val hasUnfinishedAssistance: Boolean,
    val guestsCount: Int?,
    val hasUnfinishedStay: Boolean,
    val currentStayId: Long?,
)
