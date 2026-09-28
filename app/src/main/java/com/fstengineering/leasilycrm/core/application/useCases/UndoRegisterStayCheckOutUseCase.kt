package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.models.commands.UndoCheckOutCommand
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class UndoRegisterStayCheckOutUseCase(
    private val occupiedPeriodSharedCommandRepository: OccupiedPeriodSharedCommandRepository,
    private val assistanceSharedCommandRepository: AssistanceSharedCommandRepository,
) {
    suspend operator fun invoke(command: UndoCheckOutCommand): AppResult<Unit, BaseError> {
        occupiedPeriodSharedCommandRepository.undoRegisterCheckOut(command.stayId)
            .onError { return AppResult.Error(error = it) }

        if (command.tenantId != null) {
            assistanceSharedCommandRepository.unfinishAssistanceByTenantId(command.tenantId)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
