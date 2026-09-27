package com.fstengineering.leasilycrm.profile.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.profile.application.mappers.toEntity
import com.fstengineering.leasilycrm.profile.application.models.commands.UserCommandModel
import com.fstengineering.leasilycrm.profile.application.repositories.UserCommandRepository

class WriteUserUseCase(
    private val repository: UserCommandRepository,
) : CommandUseCase<UserCommandModel> {
    override suspend fun invoke(command: UserCommandModel): AppResult<Unit, BaseError> {
        val user = command.toEntity()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )

        return repository.update(user)
    }
}
