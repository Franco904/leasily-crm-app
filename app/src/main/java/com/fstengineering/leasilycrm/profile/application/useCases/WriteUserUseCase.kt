package com.fstengineering.leasilycrm.profile.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.profile.application.mappers.toEntity
import com.fstengineering.leasilycrm.profile.application.models.commands.UserCommandModel
import com.fstengineering.leasilycrm.profile.application.repositories.UserCommandRepository
import kotlinx.collections.immutable.persistentListOf

class WriteUserUseCase(
    private val repository: UserCommandRepository,
) : CommandUseCase<UserCommandModel> {
    override suspend fun invoke(command: UserCommandModel): AppResultCompound<Unit, BusinessRuleError> {
        val user = command.toEntity()
            .fold(
                onError = { return AppResultCompound.Errors(errors = persistentListOf(it)) },
                onSuccess = { it },
            )

        return repository.update(user)
            .fold(
                onError = { AppResultCompound.Errors(errors = persistentListOf(it)) },
                onSuccess = { AppResultCompound.Success(data = Unit) },
            )
    }
}
