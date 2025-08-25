package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType
import kotlinx.serialization.Serializable

@Serializable
data class GetTenantContactOriginTypeDto(
    val id: Int,
    val name: String,
    val isActive: Boolean,
) {
    fun toDomain() = TenantContactOriginType(
        id = id,
        name = name,
        isActive = isActive,
    )
}
