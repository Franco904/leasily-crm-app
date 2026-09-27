package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface QueryUseCase<A, R> {
    suspend operator fun invoke(args: A): AppResult<R, ApplicationError>

    interface Args

    interface ReadModel
}
