package com.fstengineering.leasilycrm.signin.infra.utils

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.user.SignInUserResponseDto
import com.fstengineering.leasilycrm.signin.domain.entities.User

fun SignInUserResponseDto.toEntity() = User(
    remoteId = id,
    name = name,
    creci = creciNumber,
    profilePictureUrl = profilePictureUrl,
    contractVersion = BuildConfig.CONTRACT_VERSION,
)
