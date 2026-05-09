package com.fstengineering.leasilycrm.leadTenantRegistration.presentation.mappers

import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.commands.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantDraftFormUiState

fun LeadTenantDraftFormUiState.Loaded.toCommandModel(): LeadTenantCommandModel {
    return LeadTenantCommandModel(
        id = id,
        createDateTime = createDateTime,
        tag = tag,
        city = city,
        identityDocument = identityDocument,
        name = name,
        phone = phone,
        email = email,
        contactOrigin = contactOrigin,
        observations = observations,
    )
}
