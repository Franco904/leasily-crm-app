package com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import kotlinx.serialization.Serializable

@Serializable
data class GetPropertyCharacteristicDto(
    val id: Long,
    val name: String,
    val isActive: Boolean,
) {
    fun toDomain() = PropertyCharacteristic(
        id = id,
        name = name,
        isActive = isActive,
    )
}
