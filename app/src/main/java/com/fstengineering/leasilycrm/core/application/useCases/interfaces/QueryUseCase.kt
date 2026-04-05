package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface QueryUseCase<A : QueryUseCase.Args, R> {
    suspend operator fun invoke(args: A): AppResult<R, ApplicationError>

    interface Args

    interface ReadModel
}
