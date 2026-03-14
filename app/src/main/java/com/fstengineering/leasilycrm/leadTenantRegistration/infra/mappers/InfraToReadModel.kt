package com.fstengineering.leasilycrm.leadTenantRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.TenantReadView
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.LeadTenantReadModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.PropertyOwnerFileReadModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.PropertyOwnerReadModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun TenantReadView.toReadModel(): LeadTenantReadModel {
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
