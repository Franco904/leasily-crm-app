package com.fstengineering.leasilycrm.leadTenantRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.leadTenantRegistration.domain.entities.LeadTenant
import kotlinx.collections.immutable.toPersistentList

data class LeadTenantCommandModel(
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
) : CommandUseCase.CommandModel
