package com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos

import kotlinx.collections.immutable.ImmutableList

data class LeadTenantReadModel(
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
    val assistances: ImmutableList<AssistanceReadModel>,
    val stays: ImmutableList<StayReadModel>,
)
