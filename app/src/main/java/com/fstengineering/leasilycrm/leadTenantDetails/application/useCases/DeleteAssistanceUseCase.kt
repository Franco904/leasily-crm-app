package com.fstengineering.leasilycrm.leadTenantDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteAssistanceCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceCommandRepository
import kotlinx.collections.immutable.persistentListOf

class DeleteAssistanceUseCase(
    private val assistanceCommandRepository: AssistanceCommandRepository,
) : CommandUseCase<DeleteAssistanceCommand> {
    override suspend fun invoke(command: DeleteAssistanceCommand): AppResultCompound<Unit, BusinessRuleError> {
        assistanceCommandRepository.delete(command.id)
            .onError { e -> return AppResultCompound.Errors(persistentListOf(e)) }
        return AppResultCompound.Success(data = Unit)
    }
}
