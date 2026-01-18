package com.fstengineering.leasilycrm.core.data.dataSources.remote.responseDtos.domainTables

import kotlinx.serialization.Serializable

@Serializable
data class GetDomainTablesDto(
    val assistanceCharacteristics: List<GetAssistanceCharacteristicDto>,
    val customerSegmentTypes: List<GetCustomerSegmentTypeDto>,
    val propertyCharacteristics: List<GetPropertyCharacteristicDto>,
    val propertySeaProximityTypes: List<GetPropertySeaProximityTypeDto>,
    val propertyTypes: List<GetPropertyTypeDto>,
    val tenantContactOriginTypes: List<GetTenantContactOriginTypeDto>,
)
