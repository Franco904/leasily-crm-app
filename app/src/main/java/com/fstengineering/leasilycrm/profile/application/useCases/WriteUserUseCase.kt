package com.fstengineering.leasilycrm.profile.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.UserRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.valueObjects.Creci
import com.fstengineering.leasilycrm.profile.application.models.UserCommandModel

class WriteUserUseCase(
    private val userRepository: UserRepository,
) : CommandUseCase<UserCommandModel> {
    override suspend fun invoke(command: UserCommandModel): AppResult<Unit, SystemError> {
        val creciNumber = command.creciNumber

        if (creciNumber != null) {
            Creci.validate(creciNumber = creciNumber)
                .onError { e -> return AppResult.Error(error = e) }
        }

        return userRepository.updateUserCreciNumber(creciNumber)
    }
}
