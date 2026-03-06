package com.fstengineering.leasilycrm.profile.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class UserReadModel(
    val name: String,
    val creci: String?,
    val profilePictureUrl: String,
) : QueryUseCase.ReadModel
