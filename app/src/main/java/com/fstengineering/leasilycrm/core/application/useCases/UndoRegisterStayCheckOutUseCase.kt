package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.models.commands.UndoCheckOutCommand
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AtendimentoSharedWriteDataSource

class UndoRegisterStayCheckOutUseCase(
    private val occupiedPeriodSharedCommandRepository: OccupiedPeriodSharedCommandRepository,
    private val atendimentoSharedWriteDataSource: AtendimentoSharedWriteDataSource,
) {
    suspend operator fun invoke(command: UndoCheckOutCommand): AppResult<Unit> {
        occupiedPeriodSharedCommandRepository.undoRegisterCheckOut(command.stayId)
            .onError { return AppResult.Error(error = it) }

        if (command.tenantId != null) {
            atendimentoSharedWriteDataSource.unfinish(command.tenantId)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
