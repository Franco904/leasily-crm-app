package com.fstengineering.leasilycrm.leadTenantRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class LeadTenantReadModel(
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
) : QueryUseCase.ReadModel