package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.models.commands.UndoCheckOutCommand
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.AssistanceCommandRepository
import kotlinx.collections.immutable.persistentListOf

class UndoRegisterStayCheckOutUseCase(
    private val occupiedPeriodSharedCommandRepository: OccupiedPeriodSharedCommandRepository,
    private val assistanceCommandRepository: AssistanceCommandRepository,
) : CommandUseCase<UndoCheckOutCommand> {
    override suspend fun invoke(command: UndoCheckOutCommand): AppResultCompound<Unit, BusinessRuleError> {
        occupiedPeriodSharedCommandRepository.undoRegisterCheckOut(command.stayId)
            .onError { return AppResultCompound.Errors(persistentListOf(it)) }

        if (command.tenantId != null) {
            assistanceCommandRepository.unfinishAssistanceByTenantId(command.tenantId)
                .onError { return AppResultCompound.Errors(persistentListOf(it)) }
        }

        return AppResultCompound.Success(data = Unit)
    }
}
