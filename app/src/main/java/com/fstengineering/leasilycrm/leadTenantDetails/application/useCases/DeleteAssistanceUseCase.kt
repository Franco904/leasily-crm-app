package com.fstengineering.leasilycrm.leadTenantDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteAssistanceCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceCommandRepository

class DeleteAssistanceUseCase(
    private val assistanceCommandRepository: AssistanceCommandRepository,
) : CommandUseCase<DeleteAssistanceCommand> {
    override suspend fun invoke(command: DeleteAssistanceCommand): AppResult<Unit, BaseError> {
        return assistanceCommandRepository.delete(command.id)
    }
}
