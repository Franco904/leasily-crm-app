package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import kotlinx.serialization.Serializable

@Serializable
data class GetPropertyTypeDto(
    val id: Long,
    val name: String,
    val isActive: Boolean,
) {
    fun toDomain() = PropertyType(
        id = id,
        name = name,
        isActive = isActive,
    )
}
