package com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.user

import kotlinx.serialization.Serializable

@Serializable
data class SignInUserResponseDto(
    val id: Long,
    val name: String,
    val creciNumber: String? = null,
    val profilePictureUrl: String,
)
