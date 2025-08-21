package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import kotlinx.serialization.Serializable

@Serializable
data class GetCustomerSegmentTypeDto(
    val id: Int,
    val name: String,
) {
    fun toDomain() = CustomerSegmentType(
        id = id,
        name = name,
    )
}
