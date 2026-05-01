package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.AssistanceCommandRepository

class RegisterStayCheckOutUseCase(
    private val occupiedPeriodSharedCommandRepository: OccupiedPeriodSharedCommandRepository,
    private val assistanceCommandRepository: AssistanceCommandRepository,
) {
    suspend operator fun invoke(
        stayId: Long,
        tenantId: Long?,
    ): AppResult<Unit, ApplicationError> {
        occupiedPeriodSharedCommandRepository.registerCheckOut(stayId)
            .onError { return AppResult.Error(error = it) }

        if (tenantId != null) {
            assistanceCommandRepository.finishOpenAssistanceByTenantId(tenantId)
                .onError { return AppResult.Error(error = it) }
        }

        return AppResult.Success(data = Unit)
    }
}
