package com.fstengineering.leasilycrm.signin.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.api.responseDtos.user.SignInUserResponseDto
import com.fstengineering.leasilycrm.signin.application.models.dtos.SignInResultWriteModel

fun SignInUserResponseDto.toWriteModel() = SignInResultWriteModel(
    remoteId = id,
    name = name,
    creci = creciNumber,
    profilePictureUrl = profilePictureUrl,
    contractVersion = BuildConfig.CONTRACT_VERSION,
)
