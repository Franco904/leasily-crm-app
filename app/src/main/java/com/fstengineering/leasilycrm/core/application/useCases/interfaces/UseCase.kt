package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError

interface UseCase<W : UseCase.WriteModel, R> {
    suspend operator fun invoke(writeModel: W): AppResult<R, SystemError>

    interface WriteModel
}
