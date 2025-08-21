package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import kotlinx.serialization.Serializable

@Serializable
data class GetPropertySeaProximityTypeDto(
    val id: Int,
    val name: String,
) {
    fun toDomain() = PropertySeaProximityType(
        id = id,
        name = name,
    )
}
