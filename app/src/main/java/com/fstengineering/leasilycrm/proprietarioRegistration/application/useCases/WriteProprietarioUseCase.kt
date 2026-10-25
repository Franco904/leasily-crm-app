package com.fstengineering.leasilycrm.proprietarioRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.proprietarioRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.commands.ProprietarioCommandModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.repositories.ProprietarioCommandRepository

class WriteProprietarioUseCase(
    private val proprietarioCommandRepository: ProprietarioCommandRepository,
) {
    suspend operator fun invoke(
        command: ProprietarioCommandModel,
    ): AppResult<Unit, BaseError> {
        val proprietario = command.toEntity()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return proprietarioCommandRepository.insertOrUpdate(proprietario)
    }
}
