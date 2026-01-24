package com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestDto(
    val idToken: String,
)
