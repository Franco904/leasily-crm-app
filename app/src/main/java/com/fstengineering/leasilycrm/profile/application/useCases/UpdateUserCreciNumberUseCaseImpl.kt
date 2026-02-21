package com.fstengineering.leasilycrm.profile.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.UserRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UseCase
import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.valueObjects.Creci
import com.fstengineering.leasilycrm.profile.application.models.UserWriteModel

class UpdateUserCreciNumberUseCaseImpl(
    private val userRepository: UserRepository,
) : UseCase<UserWriteModel, Unit> {
    override suspend fun invoke(writeModel: UserWriteModel): AppResult<Unit, SystemError> {
        val creciNumber = writeModel.creciNumber

        if (creciNumber != null) {
            Creci.validate(creciNumber = creciNumber)
                .onError { e -> return AppResult.Error(error = e) }
        }

        return userRepository.updateUserCreciNumber(creciNumber)
    }
}
