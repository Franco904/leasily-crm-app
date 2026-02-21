package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError

interface RegisterStayCheckOutUseCase {
    suspend operator fun invoke(stay: Stay.Owned): AppResult<Unit, InfraError>
}
