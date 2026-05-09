package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface QueryFlowUseCase<Q, R> {
    suspend operator fun invoke(args: Q): Flow<AppResult<R, ApplicationError>>
}
