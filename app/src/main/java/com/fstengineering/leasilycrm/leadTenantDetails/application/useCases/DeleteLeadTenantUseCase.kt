package com.fstengineering.leasilycrm.leadTenantDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteLeadTenantCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantCommandRepository
import kotlinx.collections.immutable.persistentListOf

class DeleteLeadTenantUseCase(
    private val leadTenantCommandRepository: LeadTenantCommandRepository,
) : CommandUseCase<DeleteLeadTenantCommand> {
    override suspend fun invoke(command: DeleteLeadTenantCommand): AppResultCompound<Unit, BusinessRuleError> {
        leadTenantCommandRepository.delete(command.id)
            .onError { e -> return AppResultCompound.Errors(persistentListOf(e)) }
        return AppResultCompound.Success(data = Unit)
    }
}
