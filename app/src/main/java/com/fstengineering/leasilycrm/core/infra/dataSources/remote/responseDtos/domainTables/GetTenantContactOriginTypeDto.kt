package com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType
import kotlinx.serialization.Serializable

@Serializable
data class GetTenantContactOriginTypeDto(
    val id: Long,
    val name: String,
    val isActive: Boolean,
) {
    fun toDomain() = TenantContactOriginType(
        id = id,
        name = name,
        isActive = isActive,
    )
}
