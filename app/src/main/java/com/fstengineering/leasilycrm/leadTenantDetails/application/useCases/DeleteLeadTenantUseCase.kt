package com.fstengineering.leasilycrm.leadTenantDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteLeadTenantCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantCommandRepository

class DeleteLeadTenantUseCase(
    private val leadTenantCommandRepository: LeadTenantCommandRepository,
) : CommandUseCase<DeleteLeadTenantCommand> {
    override suspend fun invoke(command: DeleteLeadTenantCommand): AppResult<Unit, BaseError> {
        return leadTenantCommandRepository.delete(command.id)
    }
}
