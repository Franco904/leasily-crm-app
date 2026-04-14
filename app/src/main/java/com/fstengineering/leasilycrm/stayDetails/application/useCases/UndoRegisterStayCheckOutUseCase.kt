package com.fstengineering.leasilycrm.stayDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.stayDetails.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.stayDetails.application.repositories.OccupiedPeriodCommandRepository

class UndoRegisterStayCheckOutUseCase(
    private val occupiedPeriodCommandRepository: OccupiedPeriodCommandRepository,
    private val assistanceCommandRepository: AssistanceCommandRepository,
) {
    suspend operator fun invoke(
        stayId: Long,
        tenantId: Long?,
    ): AppResult<Unit, ApplicationError> {
        occupiedPeriodCommandRepository.undoRegisterCheckOut(stayId)
            .onError { return AppResult.Error(error = it) }

        if (tenantId != null) {
            assistanceCommandRepository.unfinishAssistanceByTenantId(tenantId)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
