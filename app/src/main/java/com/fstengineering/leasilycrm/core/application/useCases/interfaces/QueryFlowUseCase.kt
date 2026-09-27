package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import kotlinx.coroutines.flow.Flow

interface QueryFlowUseCase<Q, R> {
    suspend operator fun invoke(args: Q): Flow<AppResult<R, ApplicationError>>
}
