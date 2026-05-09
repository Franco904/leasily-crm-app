package com.fstengineering.leasilycrm.leadTenantDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceCommandRepository

class DeleteAssistanceUseCase(
    private val assistanceCommandRepository: AssistanceCommandRepository,
) {
    suspend operator fun invoke(id: Long): AppResult<Unit, ApplicationError> {
        return assistanceCommandRepository.delete(id)
    }
}
