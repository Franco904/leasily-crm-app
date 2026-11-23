package com.fstengineering.leasilycrm.core.infra.api.requestDtos.user

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestDto(
    val idToken: String,
)
