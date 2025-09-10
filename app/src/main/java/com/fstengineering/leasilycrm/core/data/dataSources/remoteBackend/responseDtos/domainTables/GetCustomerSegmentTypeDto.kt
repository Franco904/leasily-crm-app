package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import kotlinx.serialization.Serializable

@Serializable
data class GetCustomerSegmentTypeDto(
    val id: Long,
    val name: String,
    val isActive: Boolean,
) {
    fun toDomain() = CustomerSegmentType(
        id = id,
        name = name,
        isActive = isActive,
    )
}
