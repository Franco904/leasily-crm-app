package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import kotlinx.serialization.Serializable

@Serializable
data class GetPropertyCharacteristicDto(
    val id: Int,
    val name: String,
) {
    fun toDomain() = PropertyCharacteristic(
        id = id,
        name = name,
    )
}
