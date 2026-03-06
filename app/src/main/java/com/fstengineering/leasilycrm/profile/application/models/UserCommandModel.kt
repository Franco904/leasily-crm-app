package com.fstengineering.leasilycrm.profile.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.application.utils.toApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.profile.domain.entities.User

data class UserCommandModel(val creciNumber: String?) : CommandUseCase.CommandModel {
    fun toAggregate(): AppResult<User, ApplicationError> {
        return User.parse(creciNumber = creciNumber)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it) },
            )
    }
}
