package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError

interface UseCase<P : UseCase.UseCaseParam, R> {
    suspend operator fun invoke(params: P): AppResult<R, SystemError>

    interface UseCaseParam
}
