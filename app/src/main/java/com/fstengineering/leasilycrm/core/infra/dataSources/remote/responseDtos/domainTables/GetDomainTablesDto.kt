package com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.domainTables

import kotlinx.serialization.Serializable

@Serializable
data class GetDomainTablesDto(
    val assistanceCharacteristics: List<GetAssistanceCharacteristicDto>,
    val propertyCharacteristics: List<GetPropertyCharacteristicDto>,
    val propertySeaProximityTypes: List<GetPropertySeaProximityTypeDto>,
    val propertyTypes: List<GetPropertyTypeDto>,
    val tenantContactOriginTypes: List<GetTenantContactOriginTypeDto>,
)
