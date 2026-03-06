package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface QueryUseCase<Q : QueryUseCase.QueryModel, R : QueryUseCase.ReadModel> {
    suspend operator fun invoke(query: Q): AppResult<R, ApplicationError>

    interface QueryModel

    interface ReadModel
}
