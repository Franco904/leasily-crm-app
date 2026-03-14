package com.fstengineering.leasilycrm.leadTenantRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding
import com.fstengineering.leasilycrm.leadTenantRegistration.domain.entities.LeadTenant

fun LeadTenant.toLocalDataModel(): TenantEntity {
    return TenantEntity(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        tag = tag,
        name = name.value.normalizeEncoding(),
        city = city,
        identityDocument = identityDocument,
        phone = phone?.value,
        email = email?.value,
        contactOrigin = contactOrigin,
        observations = observations,
        isDeleted = false,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}
