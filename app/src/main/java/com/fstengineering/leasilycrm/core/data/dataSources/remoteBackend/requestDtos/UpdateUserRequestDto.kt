package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequestDto(
    val name: String,
    val creciNumber: String?,
)
