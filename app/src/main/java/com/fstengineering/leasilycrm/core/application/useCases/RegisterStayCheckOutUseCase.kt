package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.models.commands.RegisterCheckOutCommand
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.persistentListOf

class RegisterStayCheckOutUseCase(
    private val occupiedPeriodSharedCommandRepository: OccupiedPeriodSharedCommandRepository,
    private val assistanceSharedCommandRepository: AssistanceSharedCommandRepository,
) : CommandUseCase<RegisterCheckOutCommand> {
    override suspend fun invoke(command: RegisterCheckOutCommand): AppResultCompound<Unit, BusinessRuleError> {
        occupiedPeriodSharedCommandRepository.registerCheckOut(command.stayId)
            .onError { return AppResultCompound.Errors(persistentListOf(it)) }

        if (command.tenantId != null) {
            assistanceSharedCommandRepository.finishOpenAssistanceByTenantId(command.tenantId)
                .onError { return AppResultCompound.Errors(persistentListOf(it)) }
        }

        return AppResultCompound.Success(data = Unit)
    }
}
