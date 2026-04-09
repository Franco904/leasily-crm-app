package com.fstengineering.leasilycrm.leadTenantRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.dtos.LeadTenantReadModel

fun LeadTenantReadView.toReadModel(): LeadTenantReadModel {
    return LeadTenantReadModel(
        id = id,
        createDateTime = createDateTime,
        tag = tag,
        name = name,
        phone = phone,
        email = email,
        city = city,
        identityDocument = identityDocument,
        contactOrigin = contactOrigin,
        observations = observations,
        isLead = status == TenantStatus.Lead.id,
    )
}
