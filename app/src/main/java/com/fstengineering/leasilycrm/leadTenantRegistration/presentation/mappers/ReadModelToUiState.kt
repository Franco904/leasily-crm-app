package com.fstengineering.leasilycrm.leadTenantRegistration.presentation.mappers

import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.LeadTenantReadModel
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantDraftFormUiState

fun LeadTenantReadModel.toDraftForm() = LeadTenantDraftFormUiState.Loaded(
    id = id,
    createDateTime = createDateTime,
    tag = tag,
    name = name,
    city = city,
    identityDocument = identityDocument,
    phone = phone,
    email = email,
    contactOrigin = contactOrigin,
    observations = observations,
    isLead = isLead,
)
