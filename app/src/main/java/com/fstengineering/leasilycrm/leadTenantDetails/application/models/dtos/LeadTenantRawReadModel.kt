package com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos

data class LeadTenantRawReadModel(
    val id: Long,
    val tag: String?,
    val name: String,
    val city: String?,
    val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val contactOrigin: String?,
    val observations: String?,
    val status: Int,
)
