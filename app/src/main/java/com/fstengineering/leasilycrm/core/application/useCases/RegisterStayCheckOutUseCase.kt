package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.models.commands.RegisterCheckOutCommand
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AtendimentoSharedWriteDataSource

class RegisterStayCheckOutUseCase(
    private val occupiedPeriodSharedCommandRepository: OccupiedPeriodSharedCommandRepository,
    private val atendimentoSharedWriteDataSource: AtendimentoSharedWriteDataSource,
) {
    suspend operator fun invoke(command: RegisterCheckOutCommand): AppResult<Unit> {
        occupiedPeriodSharedCommandRepository.registerCheckOut(command.stayId)
            .onError { return AppResult.Error(error = it) }

        if (command.tenantId != null) {
            atendimentoSharedWriteDataSource.finishOpen(command.tenantId)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
