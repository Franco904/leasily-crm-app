package com.fstengineering.leasilycrm.profile.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.profile.application.models.UserCommandModel
import com.fstengineering.leasilycrm.profile.domain.entities.User

fun UserCommandModel.toEntity(): AppResult<User, DomainError> {
    return User.parse(creciNumber = creciNumber)
        .fold(
            onError = { AppResult.Error(error = it) },
            onSuccess = { AppResult.Success(data = it) },
        )
}
