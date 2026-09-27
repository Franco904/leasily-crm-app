package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError

interface CommandUseCase<C> {
    suspend operator fun invoke(command: C): AppResult<Unit, BaseError>

    interface CommandModel
}
