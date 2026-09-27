package com.fstengineering.leasilycrm.leadTenantRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.commands.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.domain.entities.LeadTenant
import kotlinx.collections.immutable.toPersistentList

fun LeadTenantCommandModel.toEntity(): AppResult<LeadTenant, ValidationErrors> {
    val errors = mutableListOf<DomainError>()

    val parsed = LeadTenant.parse(
        id = id,
        createDateTime = createDateTime,
        name = name,
        phone = phone,
        email = email,
        tag = tag,
        city = city,
        identityDocument = identityDocument,
        contactOrigin = contactOrigin,
        observations = observations,
    )
        .fold(onError = { errors.addAll(0, it.errors); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

    return AppResult.Success(data = requireNotNull(parsed))
}
