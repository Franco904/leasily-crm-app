package com.fstengineering.leasilycrm.leadTenantRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.commands.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.domain.entities.LeadTenant
import kotlinx.collections.immutable.toPersistentList

fun LeadTenantCommandModel.toEntity(): AppResultCompound<LeadTenant, DomainError> {
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
        .fold(onErrors = { errors.addAll(0, it); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

    return AppResultCompound.Success(data = requireNotNull(parsed))
}
