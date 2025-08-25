package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables

import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import kotlinx.serialization.Serializable

@Serializable
data class GetAssistanceCharacteristicDto(
    val id: Int,
    val name: String,
    val isActive: Boolean,
) {
    fun toDomain() = AssistanceCharacteristic(
        id = id,
        name = name,
        isActive = isActive,
    )
}
