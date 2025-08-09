package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestDto(
    val idToken: String,
)
