package com.fstengineering.leasilycrm.core.infra.api.requestDtos.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDto(
    val creciNumber: String? = null,
)
