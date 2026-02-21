package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError

interface DeleteStayUseCase {
    suspend operator fun invoke(
        stay: Stay,
    ): AppResult<Unit, InfraError>
}
