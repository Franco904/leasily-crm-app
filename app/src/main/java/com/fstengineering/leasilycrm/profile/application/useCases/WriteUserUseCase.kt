package com.fstengineering.leasilycrm.profile.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.UserRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.Creci
import com.fstengineering.leasilycrm.profile.application.models.UserCommandModel
import kotlinx.collections.immutable.persistentListOf

class WriteUserUseCase(
    private val userRepository: UserRepository,
) : CommandUseCase<UserCommandModel> {
    override suspend fun invoke(command: UserCommandModel): AppResultCompound<Unit, SystemError> {
        val creciNumber = command.creciNumber

        if (creciNumber != null) {
            Creci.parse(creciNumber = creciNumber)
                .onErrors { errors -> return AppResultCompound.Errors(errors = errors) }
        }

        return userRepository.updateUserCreciNumber(creciNumber)
            .fold(
                onError = { AppResultCompound.Errors(errors = persistentListOf(it)) },
                onSuccess = { AppResultCompound.Success(data = Unit) },
            )
    }
}
