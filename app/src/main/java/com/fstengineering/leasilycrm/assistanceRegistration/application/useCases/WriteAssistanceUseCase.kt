package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.commands.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold

class WriteAssistanceUseCase(
    private val assistanceCommandRepository: AssistanceCommandRepository,
) {
    suspend operator fun invoke(
        command: AssistanceCommandModel,
    ): AppResult<Unit, BaseError> {
        val assistance = command.toEntity()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return assistanceCommandRepository.insertOrUpdate(assistance)
    }
}
