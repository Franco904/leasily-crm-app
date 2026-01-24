package com.fstengineering.leasilycrm.core.data.dataSources.remote.requestDtos.user

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestDto(
    val idToken: String,
)
